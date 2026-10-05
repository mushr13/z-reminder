package com.z.reminder.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.z.reminder.MainActivity
import com.z.reminder.data.db.ReminderDao
import com.z.reminder.data.model.Reminder
import java.util.Calendar

class AlarmScheduler(
    private val context: Context,
    private val reminderDao: ReminderDao
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_ALARM_FIRE = "com.z.reminder.ACTION_ALARM_FIRE"
        const val ACTION_NAG_FIRE = "com.z.reminder.ACTION_NAG_FIRE"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }

    /**
     * Schedules an exact alarm using setAlarmClock() for maximum priority and lockscreen awareness.
     */
    fun scheduleExactAlarm(reminder: Reminder) {
        val triggerTime = reminder.snoozedUntil ?: reminder.dueAt
        val now = System.currentTimeMillis()

        // If time has already passed and is not snoozed, do not schedule past alarm
        if (triggerTime <= now) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_FIRE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val clockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)
        alarmManager.setAlarmClock(clockInfo, pendingIntent)
    }

    /**
     * Schedules the next Nagging Alarm respecting Quiet Hours (11:00 PM - 6:30 AM).
     */
    fun scheduleNagAlarm(
        reminder: Reminder,
        nagIntervalMinutes: Int = 30,
        quietStartHour: Int = 23,
        quietEndHour: Int = 6,
        quietEndMinute: Int = 30
    ) {
        val now = System.currentTimeMillis()
        var nextNagTime = now + (nagIntervalMinutes * 60 * 1000L)

        // Check if nextNagTime falls inside quiet hours
        val cal = Calendar.getInstance().apply { timeInMillis = nextNagTime }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val isDuringQuietHours = if (quietStartHour > quietEndHour) {
            // Overnight quiet hours, e.g., 23:00 to 06:30
            hour >= quietStartHour || hour < quietEndHour || (hour == quietEndHour && minute < quietEndMinute)
        } else {
            hour in quietStartHour until quietEndHour
        }

        if (isDuringQuietHours) {
            // Adjust nextNagTime to the end of quiet hours (6:30 AM)
            val wakeCal = Calendar.getInstance().apply {
                timeInMillis = nextNagTime
                if (hour >= quietStartHour) {
                    add(Calendar.DAY_OF_YEAR, 1) // 6:30 AM next day
                }
                set(Calendar.HOUR_OF_DAY, quietEndHour)
                set(Calendar.MINUTE, quietEndMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            nextNagTime = wakeCal.timeInMillis
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NAG_FIRE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt() + 100000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val clockInfo = AlarmManager.AlarmClockInfo(nextNagTime, showPendingIntent)
        alarmManager.setAlarmClock(clockInfo, pendingIntent)
    }

    /**
     * Cancels any scheduled alarm or nag for a specific reminder.
     */
    fun cancelAlarm(reminderId: Long) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_FIRE
        }
        val pendingAlarm = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingAlarm != null) {
            alarmManager.cancel(pendingAlarm)
            pendingAlarm.cancel()
        }

        // Cancel nag alarm
        val nagIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NAG_FIRE
        }
        val pendingNag = PendingIntent.getBroadcast(
            context,
            reminderId.toInt() + 100000,
            nagIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingNag != null) {
            alarmManager.cancel(pendingNag)
            pendingNag.cancel()
        }
    }

    /**
     * Idempotent function: Restores all active alarms from the database on reboot or time change.
     */
    suspend fun rescheduleAll() {
        val now = System.currentTimeMillis()
        val activeReminders = reminderDao.getPendingAlerts(now)
        activeReminders.forEach { reminder ->
            if (reminder.status == "FIRING") {
                scheduleNagAlarm(reminder, reminder.nagIntervalMinutes ?: 30)
            } else {
                scheduleExactAlarm(reminder)
            }
        }
    }
}
