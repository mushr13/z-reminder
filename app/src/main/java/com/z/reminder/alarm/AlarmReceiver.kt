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

import android.app.KeyguardManager
import android.os.PowerManager
import com.z.reminder.alert.FullScreenAlertActivity

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

        // Turn on the screen immediately when an alarm fires
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val wakeLock = powerManager?.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "zreminder:alarm_wake"
            )
            wakeLock?.acquire(15_000L) // Hold wake lock for 15s to display alert
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = reminderDao.getReminderById(reminderId)
                if (reminder != null && reminder.status != "COMPLETED") {
                    if (isTelegramBackup) {
                        // 1 minute has elapsed since phone alarm fired: only send if still actively FIRING (unhandled)
                        if (reminder.status == "FIRING") {
                            try {
                                telegramNotifier.notifyReminderBackupAlert(reminder)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        // Regular or nag alarm firing on phone
                        reminderDao.updateReminder(reminder.copy(status = "FIRING"))

                        // Show heads-up notification banner with Complete and 15m Snooze
                        notificationHelper.showFiringNotification(reminder, isNag = isNag)

                        // If screen is locked, launch full-screen alert over keyguard
                        try {
                            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
                            if (keyguardManager?.isKeyguardLocked == true) {
                                val fullScreenIntent = Intent(context, FullScreenAlertActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_NO_USER_ACTION
                                    putExtra(AlarmScheduler.EXTRA_REMINDER_ID, reminder.id)
                                }
                                context.startActivity(fullScreenIntent)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

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
