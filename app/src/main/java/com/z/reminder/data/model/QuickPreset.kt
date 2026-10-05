package com.z.reminder.data.model

enum class PresetType {
    RELATIVE_MINUTES, // e.g. +60 minutes
    FIXED_TIME_STAGGERED // e.g. 12:30 PM with +20 min auto-stagger if slot occupied
}

data class QuickPreset(
    val id: String,
    val title: String,
    val type: PresetType,
    val relativeMinutes: Int = 60,
    val targetHour: Int = 12,
    val targetMinute: Int = 30,
    val staggerMinutes: Int = 20,
    val isPinned: Boolean = true
) {
    companion object {
        fun defaultPresets(): List<QuickPreset> = listOf(
            QuickPreset(
                id = "plus_1_hour",
                title = "1 Hour",
                type = PresetType.RELATIVE_MINUTES,
                relativeMinutes = 60,
                isPinned = true
            ),
            QuickPreset(
                id = "at_office",
                title = "At Office",
                type = PresetType.FIXED_TIME_STAGGERED,
                targetHour = 12,
                targetMinute = 30,
                staggerMinutes = 20,
                isPinned = true
            ),
            QuickPreset(
                id = "at_home",
                title = "At Home",
                type = PresetType.FIXED_TIME_STAGGERED,
                targetHour = 18,
                targetMinute = 30,
                staggerMinutes = 20,
                isPinned = true
            ),
            QuickPreset(
                id = "before_sleep",
                title = "Before Sleep",
                type = PresetType.FIXED_TIME_STAGGERED,
                targetHour = 0,
                targetMinute = 30,
                staggerMinutes = 20,
                isPinned = true
            )
        )
    }
}
