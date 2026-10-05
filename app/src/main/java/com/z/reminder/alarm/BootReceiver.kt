package com.z.reminder.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.z.reminder.data.repository.ReminderRepository
import com.z.reminder.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val alarmScheduler: AlarmScheduler by inject()
    private val reminderRepository: ReminderRepository by inject()
    private val notificationHelper: NotificationHelper by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val validActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        )

        if (action in validActions) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Reschedule all active alarms
                    alarmScheduler.rescheduleAll()

                    // Reconcile and post persistent summary notification
                    val activeReminders = reminderRepository.activeReminders.firstOrNull() ?: emptyList()
                    notificationHelper.updatePersistentNotifications(activeReminders)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
