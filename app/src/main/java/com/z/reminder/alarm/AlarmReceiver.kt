package com.z.reminder.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.z.reminder.data.db.ReminderDao
import com.z.reminder.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val reminderDao: ReminderDao by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val alarmScheduler: AlarmScheduler by inject()
    private val telegramNotifier: com.z.reminder.telegram.TelegramNotifier by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(AlarmScheduler.EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val isNag = intent.action == AlarmScheduler.ACTION_NAG_FIRE
        val isTelegramBackup = intent.action == AlarmScheduler.ACTION_TELEGRAM_BACKUP_FIRE
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = reminderDao.getReminderById(reminderId)
                if (reminder != null && reminder.status != "COMPLETED") {
                    if (isTelegramBackup) {
                        // 1 minute has elapsed since phone alarm fired and task is still uncompleted!
                        try {
                            telegramNotifier.notifyReminderBackupAlert(reminder)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        // Regular or nag alarm firing on phone
                        reminderDao.updateReminder(reminder.copy(status = "FIRING"))

                        // Show urgent / nag notification with complete & snooze actions
                        notificationHelper.showFiringNotification(reminder, isNag = isNag)

                        // Schedule 1-minute backup alert to Telegram in case user misses phone alarm
                        alarmScheduler.scheduleTelegramBackup(reminder.id, 60_000L)

                        // Schedule next nag alarm (e.g. +30 mins, respecting quiet hours)
                        val nagInterval = reminder.nagIntervalMinutes ?: 30
                        alarmScheduler.scheduleNagAlarm(reminder, nagIntervalMinutes = nagInterval)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
