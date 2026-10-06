package com.z.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.z.reminder.alarm.AlarmScheduler
import com.z.reminder.data.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {

    private val reminderRepository: ReminderRepository by inject()
    private val alarmScheduler: AlarmScheduler by inject()
    private val notificationHelper: NotificationHelper by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(NotificationHelper.EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    NotificationHelper.ACTION_COMPLETE -> {
                        // Mark completed in repository (advancing repeat if recurring)
                        reminderRepository.markCompleted(reminderId)

                        // Cancel ongoing alarm/nag and notification
                        alarmScheduler.cancelAlarm(reminderId)
                        notificationHelper.cancelNotification(reminderId)

                        // Refresh persistent notification
                        val activeReminders = reminderRepository.activeReminders.firstOrNull() ?: emptyList()
                        notificationHelper.updatePersistentNotifications(activeReminders)
                    }

                    NotificationHelper.ACTION_SNOOZE -> {
                        val minutes = intent.getIntExtra(NotificationHelper.EXTRA_SNOOZE_MINUTES, 15)
                        val snoozeUntil = System.currentTimeMillis() + (minutes * 60 * 1000L)

                        reminderRepository.snoozeReminder(reminderId, snoozeUntil)
                        notificationHelper.cancelNotification(reminderId)

                        // Schedule exact alarm for the snoozed timestamp
                        val reminder = reminderRepository.getReminderById(reminderId)
                        if (reminder != null) {
                            alarmScheduler.scheduleExactAlarm(reminder)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
