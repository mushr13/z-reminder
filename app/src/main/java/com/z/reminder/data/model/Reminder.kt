package com.z.reminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.TimeZone

enum class ReminderPriority {
    NORMAL,
    HIGH
}

enum class PlaceTrigger {
    NONE,
    ON_ARRIVAL,
    AT_TIME_AND_PLACE
}

enum class AlertStyle {
    NOTIFICATION,
    FULL_SCREEN
}

enum class ReminderStatus {
    SCHEDULED,
    FIRING,
    SNOOZED,
    COMPLETED
}

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val dueAt: Long,
    val timezoneId: String = TimeZone.getDefault().id,
    
    // Repeat configuration
    val repeatUnit: String? = null,
    val repeatInterval: Int = 1,
    val repeatWeekdaysMask: Int = 0,
    val repeatMode: String = "FROM_DUE_TIME",
    val repeatEndAt: Long? = null,
    
    // Attributes
    val priority: String = ReminderPriority.NORMAL.name,
    val categoryId: Long? = null,
    val placeId: Long? = null,
    val placeTrigger: String = PlaceTrigger.NONE.name,
    
    // Alert & Nagging
    val nagIntervalMinutes: Int? = 30,
    val alertStyle: String = AlertStyle.NOTIFICATION.name,
    val backgroundId: String? = null,
    
    // Lifecycle Status
    val status: String = ReminderStatus.SCHEDULED.name,
    val snoozedUntil: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val snoozeCount: Int = 0,
    val isPinned: Boolean = false
) {
    val isCompleted: Boolean get() = status == ReminderStatus.COMPLETED.name
    val isHighPriority: Boolean get() = priority == ReminderPriority.HIGH.name
    val hasRepeat: Boolean get() = !repeatUnit.isNullOrBlank()
}
