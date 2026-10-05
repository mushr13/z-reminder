package com.z.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.z.reminder.data.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationDismissReceiver : BroadcastReceiver(), KoinComponent {

    private val reminderRepository: ReminderRepository by inject()
    private val notificationHelper: NotificationHelper by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationHelper.ACTION_REPOST_PERSISTENT) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val activeReminders = reminderRepository.activeReminders.firstOrNull() ?: emptyList()
                    if (activeReminders.isNotEmpty()) {
                        // Re-post persistent summary notification
                        notificationHelper.updatePersistentNotifications(activeReminders)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
