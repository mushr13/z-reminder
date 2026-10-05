package com.z.reminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class QuietHoursNagTest {

    private fun calculateNextNagWithQuietHours(
        currentTimeMillis: Long,
        nagIntervalMinutes: Int = 30,
        quietStartHour: Int = 23,
        quietEndHour: Int = 6,
        quietEndMinute: Int = 30
    ): Long {
        var nextNagTime = currentTimeMillis + (nagIntervalMinutes * 60 * 1000L)
        val cal = Calendar.getInstance().apply { timeInMillis = nextNagTime }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val isDuringQuietHours = if (quietStartHour > quietEndHour) {
            hour >= quietStartHour || hour < quietEndHour || (hour == quietEndHour && minute < quietEndMinute)
        } else {
            hour in quietStartHour until quietEndHour
        }

        if (isDuringQuietHours) {
            val wakeCal = Calendar.getInstance().apply {
                timeInMillis = nextNagTime
                if (hour >= quietStartHour) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                set(Calendar.HOUR_OF_DAY, quietEndHour)
                set(Calendar.MINUTE, quietEndMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            nextNagTime = wakeCal.timeInMillis
        }

        return nextNagTime
    }

    @Test
    fun testNagOutsideQuietHours() {
        // 2:00 PM (14:00)
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nextNag = calculateNextNagWithQuietHours(cal.timeInMillis, nagIntervalMinutes = 30)
        val nextCal = Calendar.getInstance().apply { timeInMillis = nextNag }

        // Must be exactly 2:30 PM
        assertEquals(14, nextCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, nextCal.get(Calendar.MINUTE))
    }

    @Test
    fun testNagEnteringQuietHoursPausesUntilMorning() {
        // 10:45 PM (22:45). +30m lands at 11:15 PM (23:15), which is in quiet hours (23:00 - 06:30)
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 45)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nextNag = calculateNextNagWithQuietHours(cal.timeInMillis, nagIntervalMinutes = 30)
        val nextCal = Calendar.getInstance().apply { timeInMillis = nextNag }

        // Must jump to 6:30 AM next day
        assertEquals(6, nextCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, nextCal.get(Calendar.MINUTE))
        assertTrue("Must be in future", nextNag > cal.timeInMillis)
    }

    @Test
    fun testNagDuringEarlyMorningQuietHours() {
        // 3:00 AM (03:00). Quiet hours active until 06:30
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 3)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nextNag = calculateNextNagWithQuietHours(cal.timeInMillis, nagIntervalMinutes = 30)
        val nextCal = Calendar.getInstance().apply { timeInMillis = nextNag }

        // Must jump to 6:30 AM
        assertEquals(6, nextCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, nextCal.get(Calendar.MINUTE))
    }
}
