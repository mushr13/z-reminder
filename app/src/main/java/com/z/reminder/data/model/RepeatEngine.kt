package com.z.reminder.data.model

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

object RepeatEngine {

    /**
     * Calculates the next epoch timestamp (in milliseconds) for a recurring reminder.
     * 
     * @param currentDueEpoch Current due time in epoch millis
     * @param timezoneId Timezone identifier (e.g., "Asia/Dhaka", "UTC")
     * @param rule The repeat rule configuration
     * @param repeatEndAt Optional expiration timestamp
     * @param completedAt Optional completion timestamp if repeat mode is FROM_COMPLETION
     * @param now Current reference time (defaults to System.currentTimeMillis)
     * @return Next epoch millis, or null if the repeat has expired (past repeatEndAt)
     */
    fun calculateNextDueTime(
        currentDueEpoch: Long,
        timezoneId: String = ZoneId.systemDefault().id,
        rule: RepeatRule?,
        repeatEndAt: Long? = null,
        completedAt: Long? = null,
        now: Long = System.currentTimeMillis()
    ): Long? {
        if (rule == null) return null

        val zoneId = try {
            ZoneId.of(timezoneId)
        } catch (e: Exception) {
            ZoneId.systemDefault()
        }

        // Base time to calculate from
        val baseEpoch = if (rule.mode == RepeatMode.FROM_COMPLETION && completedAt != null) {
            completedAt
        } else {
            currentDueEpoch
        }

        val baseZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(baseEpoch), zoneId)
        var nextZdt = when (rule.unit) {
            RepeatUnit.MINUTE -> {
                val interval = rule.interval.coerceAtLeast(1)
                baseZdt.plusMinutes(interval.toLong())
            }
            RepeatUnit.HOUR -> {
                val interval = rule.interval.coerceAtLeast(1)
                baseZdt.plusHours(interval.toLong())
            }
            RepeatUnit.DAY -> {
                val interval = rule.interval.coerceAtLeast(1)
                baseZdt.plusDays(interval.toLong())
            }
            RepeatUnit.WEEK -> {
                calculateNextWeeklyOccurrence(baseZdt, rule)
            }
            RepeatUnit.MONTH -> {
                calculateNextMonthlyOccurrence(baseZdt, rule)
            }
        }

        // Skip missed backlog occurrences: Advance until nextZdt is strictly in the future relative to 'now'
        while (nextZdt.toInstant().toEpochMilli() <= now) {
            nextZdt = when (rule.unit) {
                RepeatUnit.MINUTE -> nextZdt.plusMinutes(rule.interval.coerceAtLeast(1).toLong())
                RepeatUnit.HOUR -> nextZdt.plusHours(rule.interval.coerceAtLeast(1).toLong())
                RepeatUnit.DAY -> nextZdt.plusDays(rule.interval.coerceAtLeast(1).toLong())
                RepeatUnit.WEEK -> calculateNextWeeklyOccurrence(nextZdt, rule)
                RepeatUnit.MONTH -> calculateNextMonthlyOccurrence(nextZdt, rule)
            }
        }

        val nextEpoch = nextZdt.toInstant().toEpochMilli()

        // Verify repeatEndAt limit
        if (repeatEndAt != null && nextEpoch > repeatEndAt) {
            return null
        }

        return nextEpoch
    }

    /**
     * Calculates the next weekly occurrence respecting the selected weekdays bitmask.
     * Bit 1 = Monday (DayOfWeek.MONDAY.value) ... Bit 7 = Sunday (DayOfWeek.SUNDAY.value)
     */
    private fun calculateNextWeeklyOccurrence(currentZdt: ZonedDateTime, rule: RepeatRule): ZonedDateTime {
        val mask = rule.weekdaysMask

        // If no specific days selected, repeat simply by rule.interval weeks
        if (mask == 0) {
            return currentZdt.plusWeeks(rule.interval.coerceAtLeast(1).toLong())
        }

        val currentDay = currentZdt.dayOfWeek.value // 1 (Mon) to 7 (Sun)
        
        // Check remaining days in the current week (from tomorrow to Sunday)
        for (dayOffset in 1..7) {
            val candidateDay = ((currentDay - 1 + dayOffset) % 7) + 1
            if ((mask and (1 shl candidateDay)) != 0) {
                val candidateZdt = currentZdt.plusDays(dayOffset.toLong())
                // If the candidate lands in a new week, ensure we apply interval weeks
                val weeksBetween = if (candidateDay <= currentDay) rule.interval.coerceAtLeast(1) else 1
                return if (candidateDay <= currentDay && rule.interval > 1) {
                    currentZdt.plusWeeks((rule.interval - 1).toLong()).plusDays(dayOffset.toLong())
                } else {
                    candidateZdt
                }
            }
        }

        // Fallback: advance by interval weeks
        return currentZdt.plusWeeks(rule.interval.coerceAtLeast(1).toLong())
    }

    /**
     * Calculates next monthly occurrence with automatic month-end day clamping.
     * e.g., Jan 31 -> Feb 28 (or 29 in leap year), Mar 31 -> Apr 30
     */
    private fun calculateNextMonthlyOccurrence(currentZdt: ZonedDateTime, rule: RepeatRule): ZonedDateTime {
        val interval = rule.interval.coerceAtLeast(1)
        val targetMonth = currentZdt.toLocalDate().plusMonths(interval.toLong())
        val originalDayOfMonth = currentZdt.dayOfMonth

        // Clamp day of month to maximum days in the target year-month
        val maxDaysInTargetMonth = YearMonth.from(targetMonth).lengthOfMonth()
        val clampedDay = originalDayOfMonth.coerceAtMost(maxDaysInTargetMonth)

        val targetLocalDate = LocalDate.of(targetMonth.year, targetMonth.month, clampedDay)
        return ZonedDateTime.of(
            LocalDateTime.of(targetLocalDate, currentZdt.toLocalTime()),
            currentZdt.zone
        )
    }
}
