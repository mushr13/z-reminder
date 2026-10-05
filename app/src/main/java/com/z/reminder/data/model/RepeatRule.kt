package com.z.reminder.data.model

enum class RepeatUnit {
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH
}

enum class RepeatMode {
    FROM_DUE_TIME,
    FROM_COMPLETION
}

data class RepeatRule(
    val unit: RepeatUnit,
    val interval: Int = 1,
    val weekdaysMask: Int = 0, // Bitmask: bit 1 = Monday, ..., bit 7 = Sunday
    val mode: RepeatMode = RepeatMode.FROM_DUE_TIME
) {
    fun isDaySelected(dayOfWeek: Int): Boolean {
        return (weekdaysMask and (1 shl dayOfWeek)) != 0
    }

    companion object {
        fun createWeekly(days: List<Int>, interval: Int = 1): RepeatRule {
            var mask = 0
            days.forEach { mask = mask or (1 shl it) }
            return RepeatRule(unit = RepeatUnit.WEEK, interval = interval, weekdaysMask = mask)
        }
    }
}
