package com.z.reminder

import com.z.reminder.data.model.RepeatEngine
import com.z.reminder.data.model.RepeatMode
import com.z.reminder.data.model.RepeatRule
import com.z.reminder.data.model.RepeatUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RepeatEngineTest {

    private val zoneId = ZoneId.of("Asia/Dhaka")

    @Test
    fun testMinuteRepeat() {
        // Due at 10:00 AM, repeat every 15 minutes
        val due = ZonedDateTime.of(2026, 10, 6, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val now = due // current time is at due time
        val rule = RepeatRule(unit = RepeatUnit.MINUTE, interval = 15)

        val next = RepeatEngine.calculateNextDueTime(due, zoneId.id, rule, now = now)
        assertNotNull(next)

        val nextZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(next!!), zoneId)
        assertEquals(10, nextZdt.hour)
        assertEquals(15, nextZdt.minute)
    }

    @Test
    fun testHourlyRepeat() {
        // Due at 2:00 PM, repeat every 2 hours
        val due = ZonedDateTime.of(2026, 10, 6, 14, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val now = due
        val rule = RepeatRule(unit = RepeatUnit.HOUR, interval = 2)

        val next = RepeatEngine.calculateNextDueTime(due, zoneId.id, rule, now = now)
        assertNotNull(next)

        val nextZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(next!!), zoneId)
        assertEquals(16, nextZdt.hour)
        assertEquals(0, nextZdt.minute)
    }

    @Test
    fun testDailyRepeat() {
        // Due at 9:00 AM on Oct 6, repeat every 1 day
        val due = ZonedDateTime.of(2026, 10, 6, 9, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val now = due
        val rule = RepeatRule(unit = RepeatUnit.DAY, interval = 1)

        val next = RepeatEngine.calculateNextDueTime(due, zoneId.id, rule, now = now)
        assertNotNull(next)

        val nextZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(next!!), zoneId)
        assertEquals(7, nextZdt.dayOfMonth)
        assertEquals(9, nextZdt.hour)
    }

    @Test
    fun testWeeklyWithWeekdayMask() {
        // Due on Monday Oct 5 2026, repeat on Monday (1), Wednesday (3), Friday (5)
        // Monday = 1, Wed = 3, Fri = 5
        val mask = (1 shl 1) or (1 shl 3) or (1 shl 5)
        val rule = RepeatRule(unit = RepeatUnit.WEEK, interval = 1, weekdaysMask = mask)

        val monday = ZonedDateTime.of(2026, 10, 5, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        
        // Next after Monday should be Wednesday Oct 7
        val nextAfterMon = RepeatEngine.calculateNextDueTime(monday, zoneId.id, rule, now = monday)
        assertNotNull(nextAfterMon)
        val wedZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextAfterMon!!), zoneId)
        assertEquals(7, wedZdt.dayOfMonth) // Oct 7 is Wednesday

        // Next after Wednesday should be Friday Oct 9
        val nextAfterWed = RepeatEngine.calculateNextDueTime(nextAfterMon, zoneId.id, rule, now = nextAfterMon)
        assertNotNull(nextAfterWed)
        val friZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextAfterWed!!), zoneId)
        assertEquals(9, friZdt.dayOfMonth) // Oct 9 is Friday

        // Next after Friday should wrap to next Monday Oct 12
        val nextAfterFri = RepeatEngine.calculateNextDueTime(nextAfterWed, zoneId.id, rule, now = nextAfterWed)
        assertNotNull(nextAfterFri)
        val nextMonZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextAfterFri!!), zoneId)
        assertEquals(12, nextMonZdt.dayOfMonth) // Oct 12 is Monday
    }

    @Test
    fun testMonthEndClamping() {
        // Due on January 31, 2026 (non-leap year, Feb has 28 days)
        val jan31 = ZonedDateTime.of(2026, 1, 31, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val rule = RepeatRule(unit = RepeatUnit.MONTH, interval = 1)

        val nextAfterJan31 = RepeatEngine.calculateNextDueTime(jan31, zoneId.id, rule, now = jan31)
        assertNotNull(nextAfterJan31)

        val febZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextAfterJan31!!), zoneId)
        assertEquals(2, febZdt.monthValue)
        assertEquals(28, febZdt.dayOfMonth) // Clamped from 31 to 28!

        // March 31 -> April 30 (April has 30 days)
        val mar31 = ZonedDateTime.of(2026, 3, 31, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val nextAfterMar31 = RepeatEngine.calculateNextDueTime(mar31, zoneId.id, rule, now = mar31)
        assertNotNull(nextAfterMar31)

        val aprZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextAfterMar31!!), zoneId)
        assertEquals(4, aprZdt.monthValue)
        assertEquals(30, aprZdt.dayOfMonth) // Clamped to 30!
    }

    @Test
    fun testSkipMissedBacklog() {
        // Reminder was due 5 days ago (Oct 1). Current time is Oct 6.
        val pastDue = ZonedDateTime.of(2026, 10, 1, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val now = ZonedDateTime.of(2026, 10, 6, 14, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val rule = RepeatRule(unit = RepeatUnit.DAY, interval = 1)

        val next = RepeatEngine.calculateNextDueTime(pastDue, zoneId.id, rule, now = now)
        assertNotNull(next)

        // Must skip past days and advance directly to Oct 7 at 10:00 AM!
        val nextZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(next!!), zoneId)
        assertTrue("Next occurrence must be in the future relative to now", next > now)
        assertEquals(7, nextZdt.dayOfMonth)
        assertEquals(10, nextZdt.hour)
    }

    @Test
    fun testRepeatFromCompletion() {
        // Due was at 9:00 AM, but user completed it at 11:30 AM.
        // Repeat every 2 hours from completion
        val due = ZonedDateTime.of(2026, 10, 6, 9, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val completedAt = ZonedDateTime.of(2026, 10, 6, 11, 30, 0, 0, zoneId).toInstant().toEpochMilli()
        val rule = RepeatRule(unit = RepeatUnit.HOUR, interval = 2, mode = RepeatMode.FROM_COMPLETION)

        val next = RepeatEngine.calculateNextDueTime(due, zoneId.id, rule, completedAt = completedAt, now = completedAt)
        assertNotNull(next)

        val nextZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(next!!), zoneId)
        assertEquals(13, nextZdt.hour)
        assertEquals(30, nextZdt.minute) // 11:30 + 2h = 1:30 PM (13:30)
    }

    @Test
    fun testRepeatEndAtExpiration() {
        val due = ZonedDateTime.of(2026, 10, 6, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        val endAt = ZonedDateTime.of(2026, 10, 6, 11, 0, 0, 0, zoneId).toInstant().toEpochMilli()
        // Repeat every 2 hours (next would be 12:00, which exceeds endAt)
        val rule = RepeatRule(unit = RepeatUnit.HOUR, interval = 2)

        val next = RepeatEngine.calculateNextDueTime(due, zoneId.id, rule, repeatEndAt = endAt, now = due)
        assertNull("Should return null when next occurrence exceeds repeatEndAt", next)
    }
}
