package com.z.reminder.places

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OfficeStateManagerTest {

    private lateinit var manager: OfficeStateManager
    private val debounce = 3 * 60 * 1000L           // 3 min
    private val checkInDelay = 60 * 60 * 1000L      // 60 min
    private val shortExitTolerance = 30 * 60 * 1000L // 30 min

    @Before
    fun setup() {
        manager = OfficeStateManager(
            debounceMs = debounce,
            checkInDelayMs = checkInDelay,
            shortExitToleranceMs = shortExitTolerance
        )
    }

    @Test
    fun `test initial state is ABSENT`() {
        assertEquals(OfficePresenceStatus.ABSENT, manager.state.status)
        assertFalse(manager.state.hasTriggeredArrivalPrompt)
        assertFalse(manager.state.hasTriggered60MinCheckIn)
    }

    @Test
    fun `test false alarm disconnect within 3 minutes reverts to ABSENT without triggering alert`() {
        val t0 = 1000000L
        val event1 = manager.onPresenceDetected(t0)
        assertEquals(OfficeStateManager.Event.None, event1)
        assertEquals(OfficePresenceStatus.ARRIVING, manager.state.status)

        // 2 minutes later - disconnected (walking past office)
        val event2 = manager.onPresenceLost(t0 + 2 * 60 * 1000L)
        assertEquals(OfficeStateManager.Event.None, event2)
        assertEquals(OfficePresenceStatus.ABSENT, manager.state.status)
        assertFalse(manager.state.hasTriggeredArrivalPrompt)
    }

    @Test
    fun `test arrival confirms after 3 minutes and fires ArrivalDebouncePassed`() {
        val t0 = 1000000L
        manager.onPresenceDetected(t0)

        // 3 minutes later
        val event = manager.onPresenceDetected(t0 + debounce)
        assertEquals(OfficeStateManager.Event.ArrivalDebouncePassed, event)
        assertEquals(OfficePresenceStatus.CONFIRMED, manager.state.status)
        assertTrue(manager.state.hasTriggeredArrivalPrompt)
    }

    @Test
    fun `test 60 minute continuous stay fires Trigger60MinCheckIn once`() {
        val t0 = 1000000L
        manager.onPresenceDetected(t0)
        manager.onPresenceDetected(t0 + debounce) // Confirmed

        // 59 minutes later - should not fire check-in yet
        val event59m = manager.evaluate(t0 + 59 * 60 * 1000L)
        assertEquals(OfficeStateManager.Event.None, event59m)
        assertFalse(manager.state.hasTriggered60MinCheckIn)

        // 60 minutes later - fires check-in!
        val event60m = manager.evaluate(t0 + checkInDelay)
        assertEquals(OfficeStateManager.Event.Trigger60MinCheckIn, event60m)
        assertTrue(manager.state.hasTriggered60MinCheckIn)

        // Subsequent evaluations do not re-fire
        val event90m = manager.evaluate(t0 + 90 * 60 * 1000L)
        assertEquals(OfficeStateManager.Event.None, event90m)
    }

    @Test
    fun `test short exit for 15 minutes coffee break preserves visit state and timer`() {
        val t0 = 1000000L
        manager.onPresenceDetected(t0)
        manager.onPresenceDetected(t0 + debounce) // Confirmed

        // 40 minutes into the day, disconnects for coffee
        val tCoffee = t0 + 40 * 60 * 1000L
        manager.onPresenceLost(tCoffee)
        assertEquals(OfficePresenceStatus.DEPARTING, manager.state.status)

        // Returns 15 minutes later (t0 + 55 min)
        val tReturn = tCoffee + 15 * 60 * 1000L
        val returnEvent = manager.onPresenceDetected(tReturn)
        assertEquals(OfficeStateManager.Event.None, returnEvent)
        assertEquals(OfficePresenceStatus.CONFIRMED, manager.state.status)

        // At t0 + 60 minutes, check-in alert triggers accurately without reset
        val event60m = manager.evaluate(t0 + checkInDelay)
        assertEquals(OfficeStateManager.Event.Trigger60MinCheckIn, event60m)
    }

    @Test
    fun `test exit exceeding 30 minutes confirms departure and resets visit`() {
        val t0 = 1000000L
        manager.onPresenceDetected(t0)
        manager.onPresenceDetected(t0 + debounce)

        // Leaves at 5:00 PM
        val tLeave = t0 + 5 * 3600 * 1000L
        manager.onPresenceLost(tLeave)
        assertEquals(OfficePresenceStatus.DEPARTING, manager.state.status)

        // 35 minutes later, departure confirmed
        val departEvent = manager.evaluate(tLeave + 35 * 60 * 1000L)
        assertEquals(OfficeStateManager.Event.DepartureConfirmed, departEvent)
        assertEquals(OfficePresenceStatus.ABSENT, manager.state.status)
    }
}
