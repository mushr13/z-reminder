package com.z.reminder.data.repository

import com.z.reminder.alarm.AlarmScheduler
import com.z.reminder.data.db.ReminderDao
import com.z.reminder.data.model.PresetType
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.model.RepeatEngine
import com.z.reminder.data.model.RepeatMode
import com.z.reminder.data.model.RepeatRule
import com.z.reminder.data.model.RepeatUnit
import com.z.reminder.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler,
    private val notificationHelper: NotificationHelper
) {
    val activeReminders: Flow<List<Reminder>> = reminderDao.getAllActiveReminders()
    val completedReminders: Flow<List<Reminder>> = reminderDao.getCompletedReminders()

    fun getRemindersForDay(startOfDay: Long, endOfDay: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForDay(startOfDay, endOfDay)
    }

    fun getOverdueReminders(now: Long = System.currentTimeMillis()): Flow<List<Reminder>> {
        return reminderDao.getOverdueReminders(now)
    }

    fun getUpcomingReminders(fromTime: Long = System.currentTimeMillis()): Flow<List<Reminder>> {
        return reminderDao.getUpcomingReminders(fromTime)
    }

    suspend fun getReminderById(id: Long): Reminder? = reminderDao.getReminderById(id)

    suspend fun insertReminder(reminder: Reminder): Long {
        val id = reminderDao.insertReminder(reminder)
        val created = reminder.copy(id = id)
        alarmScheduler.scheduleExactAlarm(created)
        reconcilePersistentNotifications()
        return id
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
        alarmScheduler.scheduleExactAlarm(reminder)
        reconcilePersistentNotifications()
    }

    suspend fun deleteReminder(reminder: Reminder) {
        alarmScheduler.cancelAlarm(reminder.id)
        notificationHelper.cancelNotification(reminder.id)
        reminderDao.deleteReminder(reminder)
        reconcilePersistentNotifications()
    }

    suspend fun markCompleted(id: Long) {
        val reminder = reminderDao.getReminderById(id) ?: return
        val now = System.currentTimeMillis()

        alarmScheduler.cancelAlarm(id)
        notificationHelper.cancelNotification(id)

        if (reminder.hasRepeat) {
            val rule = RepeatRule(
                unit = RepeatUnit.valueOf(reminder.repeatUnit!!),
                interval = reminder.repeatInterval,
                weekdaysMask = reminder.repeatWeekdaysMask,
                mode = try { RepeatMode.valueOf(reminder.repeatMode) } catch (e: Exception) { RepeatMode.FROM_DUE_TIME }
            )

            val nextDue = RepeatEngine.calculateNextDueTime(
                currentDueEpoch = reminder.dueAt,
                timezoneId = reminder.timezoneId,
                rule = rule,
                repeatEndAt = reminder.repeatEndAt,
                completedAt = now,
                now = now
            )

            // Save completed record in history
            val completedHistoryRecord = reminder.copy(
                id = 0,
                status = "COMPLETED",
                completedAt = now
            )
            reminderDao.insertReminder(completedHistoryRecord)

            if (nextDue != null) {
                // Advance active reminder to next occurrence
                val nextOccurrence = reminder.copy(
                    dueAt = nextDue,
                    status = "SCHEDULED",
                    snoozedUntil = null,
                    snoozeCount = 0,
                    completedAt = null
                )
                reminderDao.updateReminder(nextOccurrence)
                alarmScheduler.scheduleExactAlarm(nextOccurrence)
            } else {
                // Repeat rule expired
                reminderDao.markCompleted(id, now)
            }
        } else {
            reminderDao.markCompleted(id, now)
        }
        reconcilePersistentNotifications()
    }

    suspend fun duplicateReminder(reminder: Reminder): Long {
        val duplicate = reminder.copy(
            id = 0,
            title = if (reminder.title.endsWith("(Copy)")) reminder.title else "${reminder.title} (Copy)",
            status = "SCHEDULED",
            snoozedUntil = null,
            snoozeCount = 0,
            completedAt = null,
            createdAt = System.currentTimeMillis()
        )
        val newId = reminderDao.insertReminder(duplicate)
        val created = duplicate.copy(id = newId)
        alarmScheduler.scheduleExactAlarm(created)
        reconcilePersistentNotifications()
        return newId
    }

    suspend fun restoreCompleted(id: Long) {
        reminderDao.restoreCompleted(id)
        val restored = reminderDao.getReminderById(id)
        if (restored != null) {
            alarmScheduler.scheduleExactAlarm(restored)
        }
        reconcilePersistentNotifications()
    }

    suspend fun snoozeReminder(id: Long, snoozedUntil: Long) {
        reminderDao.snoozeReminder(id, snoozedUntil)
        notificationHelper.cancelNotification(id)
        val reminder = reminderDao.getReminderById(id)
        if (reminder != null) {
            val snoozed = reminder.copy(snoozedUntil = snoozedUntil, status = "SNOOZED")
            alarmScheduler.scheduleExactAlarm(snoozed)
        }
        reconcilePersistentNotifications()
    }

    suspend fun reconcilePersistentNotifications() {
        val active = activeReminders.firstOrNull() ?: emptyList()
        notificationHelper.updatePersistentNotifications(active)
        com.z.reminder.widget.TodayAppWidgetProvider.updateAllWidgets(notificationHelper.context)
    }

    /**
     * Calculates the scheduled time for a Quick Preset.
     * For FIXED_TIME_STAGGERED (e.g. Office at 12:30 PM):
     * If a reminder already exists at or near that time today, it automatically staggers
     * forward by staggerMinutes (+20 min) so alerts don't clash.
     */
    suspend fun calculatePresetTargetTime(preset: QuickPreset): Long {
        val now = System.currentTimeMillis()
        if (preset.type == PresetType.RELATIVE_MINUTES) {
            return now + (preset.relativeMinutes * 60 * 1000L)
        }

        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, preset.targetHour)
            set(Calendar.MINUTE, preset.targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time today has already elapsed, schedule for tomorrow
        if (cal.timeInMillis <= now) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Query existing reminders for that day
        val startOfDayCal = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endOfDayCal = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val existing = reminderDao.getRemindersForDaySnapshot(
            startOfDay = startOfDayCal.timeInMillis,
            endOfDay = endOfDayCal.timeInMillis
        )

        val staggerMillis = preset.staggerMinutes * 60 * 1000L
        var targetTime = cal.timeInMillis

        // Auto-spacing: Check if any existing reminder is within 10 minutes of targetTime
        // If so, bump by staggerMinutes until a free window is found
        while (existing.any { Math.abs(it.dueAt - targetTime) < (10 * 60 * 1000L) }) {
            targetTime += staggerMillis
        }

        return targetTime
    }
}
