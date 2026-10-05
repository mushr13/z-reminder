package com.z.reminder.places

/**
 * State of the user relative to an office or tracked place.
 */
enum class OfficePresenceStatus {
    ABSENT,
    ARRIVING,      // Connected, waiting for 3-minute debounce
    CONFIRMED,     // Confirmed present at office (> 3 minutes)
    DEPARTING      // Temporarily disconnected, within 30-minute short-exit tolerance
}

data class OfficePresenceState(
    val status: OfficePresenceStatus = OfficePresenceStatus.ABSENT,
    val firstConnectedTime: Long = 0L,
    val confirmedArrivalTime: Long = 0L,
    val disconnectTime: Long = 0L,
    val hasTriggeredArrivalPrompt: Boolean = false,
    val hasTriggered60MinCheckIn: Boolean = false
)

/**
 * Pure state machine for office presence tracking.
 * - 3-minute arrival debounce filter (prevents false alarms from briefly walking past)
 * - 60-minute continuous stay check-in alert
 * - 30-minute short-exit tolerance (stepping out for lunch or coffee doesn't reset visit)
 */
class OfficeStateManager(
    val debounceMs: Long = 3 * 60 * 1000L,              // 3 minutes
    val checkInDelayMs: Long = 60 * 60 * 1000L,         // 60 minutes
    val shortExitToleranceMs: Long = 30 * 60 * 1000L    // 30 minutes
) {
    var state = OfficePresenceState()
        private set

    sealed class Event {
        object None : Event()
        object ArrivalDebouncePassed : Event()
        object Trigger60MinCheckIn : Event()
        object DepartureConfirmed : Event()
    }

    /**
     * Call when a target office Wi-Fi network or geofence is detected.
     */
    fun onPresenceDetected(currentTime: Long): Event {
        return when (state.status) {
            OfficePresenceStatus.ABSENT -> {
                state = state.copy(
                    status = OfficePresenceStatus.ARRIVING,
                    firstConnectedTime = currentTime,
                    disconnectTime = 0L
                )
                Event.None
            }
            OfficePresenceStatus.ARRIVING -> {
                if (currentTime - state.firstConnectedTime >= debounceMs) {
                    state = state.copy(
                        status = OfficePresenceStatus.CONFIRMED,
                        confirmedArrivalTime = state.firstConnectedTime,
                        hasTriggeredArrivalPrompt = true
                    )
                    Event.ArrivalDebouncePassed
                } else {
                    Event.None
                }
            }
            OfficePresenceStatus.DEPARTING -> {
                // Reconnected within short-exit tolerance! Resume CONFIRMED without resetting timers.
                val disconnectedDuration = currentTime - state.disconnectTime
                if (disconnectedDuration < shortExitToleranceMs) {
                    state = state.copy(
                        status = OfficePresenceStatus.CONFIRMED,
                        disconnectTime = 0L
                    )
                    // Check if 60 minutes have elapsed while away/returned
                    check60MinTimer(currentTime)
                } else {
                    // Exceeded tolerance; treat as fresh arrival
                    state = OfficePresenceState(
                        status = OfficePresenceStatus.ARRIVING,
                        firstConnectedTime = currentTime
                    )
                    Event.None
                }
            }
            OfficePresenceStatus.CONFIRMED -> {
                check60MinTimer(currentTime)
            }
        }
    }

    /**
     * Call when target network or geofence is lost / disconnected.
     */
    fun onPresenceLost(currentTime: Long): Event {
        return when (state.status) {
            OfficePresenceStatus.ARRIVING -> {
                // False alarm: disconnected before 3-minute debounce passed
                state = OfficePresenceState(status = OfficePresenceStatus.ABSENT)
                Event.None
            }
            OfficePresenceStatus.CONFIRMED -> {
                state = state.copy(
                    status = OfficePresenceStatus.DEPARTING,
                    disconnectTime = currentTime
                )
                Event.None
            }
            OfficePresenceStatus.DEPARTING -> {
                if (currentTime - state.disconnectTime >= shortExitToleranceMs) {
                    state = OfficePresenceState(status = OfficePresenceStatus.ABSENT)
                    Event.DepartureConfirmed
                } else {
                    Event.None
                }
            }
            OfficePresenceStatus.ABSENT -> Event.None
        }
    }

    /**
     * Periodic evaluation (e.g. background heartbeat or timer check).
     */
    fun evaluate(currentTime: Long): Event {
        return when (state.status) {
            OfficePresenceStatus.ARRIVING -> {
                if (currentTime - state.firstConnectedTime >= debounceMs) {
                    state = state.copy(
                        status = OfficePresenceStatus.CONFIRMED,
                        confirmedArrivalTime = state.firstConnectedTime,
                        hasTriggeredArrivalPrompt = true
                    )
                    Event.ArrivalDebouncePassed
                } else {
                    Event.None
                }
            }
            OfficePresenceStatus.CONFIRMED -> {
                check60MinTimer(currentTime)
            }
            OfficePresenceStatus.DEPARTING -> {
                if (currentTime - state.disconnectTime >= shortExitToleranceMs) {
                    state = OfficePresenceState(status = OfficePresenceStatus.ABSENT)
                    Event.DepartureConfirmed
                } else {
                    Event.None
                }
            }
            OfficePresenceStatus.ABSENT -> Event.None
        }
    }

    private fun check60MinTimer(currentTime: Long): Event {
        if (!state.hasTriggered60MinCheckIn && (currentTime - state.confirmedArrivalTime >= checkInDelayMs)) {
            state = state.copy(hasTriggered60MinCheckIn = true)
            return Event.Trigger60MinCheckIn
        }
        return Event.None
    }

    fun reset() {
        state = OfficePresenceState()
    }
}
