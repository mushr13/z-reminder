package com.z.reminder.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.z.reminder.MainActivity
import com.z.reminder.R
import com.z.reminder.data.model.Reminder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationHelper(val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_URGENT = "z_reminder_alarm_heads_up_v3"
        const val CHANNEL_NAG = "z_reminder_nag_v2"
        const val CHANNEL_PERSISTENT = "z_reminder_pinned_tasks_v2"
        const val CHANNEL_OFFICE = "z_reminder_office"

        const val SUMMARY_NOTIFICATION_ID = 99999
        const val ACTION_COMPLETE = "com.z.reminder.ACTION_COMPLETE"
        const val ACTION_SNOOZE = "com.z.reminder.ACTION_SNOOZE"
        const val ACTION_REPOST_PERSISTENT = "com.z.reminder.ACTION_REPOST_PERSISTENT"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_SNOOZE_MINUTES = "extra_snooze_minutes"
    }

    init {
        createNotificationChannels()
        notificationManager.cancel(SUMMARY_NOTIFICATION_ID)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Remove legacy channels so Android doesn't retain old, lower importance settings
            try {
                notificationManager.deleteNotificationChannel("z_reminder_alarm_heads_up_v2")
                notificationManager.deleteNotificationChannel("z_reminder_persistent")
            } catch (e: Exception) {
                // Ignore if legacy channel does not exist
            }

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // 1. Urgent Reminders & Heads-Up Channel (IMPORTANCE_HIGH = Level 4 for top banner pop-down)
            val urgentChannel = NotificationChannel(
                CHANNEL_URGENT,
                "Urgent Reminders & Heads-Up Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarms and full heads-up banner alerts while in use"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            // 2. Nagging Alerts Channel (High Importance)
            val nagChannel = NotificationChannel(
                CHANNEL_NAG,
                "Nagging Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Follow-up nagging alerts until a task is completed"
                enableVibration(true)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 3. Persistent Pinned Reminders Channel (Default Importance - Level 3 for notification shade)
            val persistentChannel = NotificationChannel(
                CHANNEL_PERSISTENT,
                "Pinned Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Sticky pinned reminders kept in the notification panel"
                setShowBadge(true)
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 4. Office Check-ins
            val officeChannel = NotificationChannel(
                CHANNEL_OFFICE,
                "Office Check-ins",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders and check-in prompts while at the office"
            }

            notificationManager.createNotificationChannels(
                listOf(urgentChannel, nagChannel, persistentChannel, officeChannel)
            )
        }
    }

    /**
     * Shows a firing reminder heads-up notification with Complete and 15m Snooze action buttons,
     * and fullScreenIntent attached to wake lockscreen.
     */
    fun showFiringNotification(reminder: Reminder, isNag: Boolean = false) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Complete Action Intent
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_COMPLETE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt() * 10 + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze 15m Action Intent (15 minutes as requested)
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_SNOOZE_MINUTES, 15)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt() * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isNag) CHANNEL_NAG else CHANNEL_URGENT
        val titlePrefix = if (isNag) "⏰ [Nagging] " else ""

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dueStr = timeFormat.format(Date(reminder.dueAt))

        val contentText = if (reminder.notes.isNotBlank()) {
            "$dueStr • ${reminder.notes}"
        } else {
            "Scheduled for $dueStr"
        }

        // Full Screen Alert Intent for lockscreen wake-up
        val fullScreenIntent = Intent(context, com.z.reminder.alert.FullScreenAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt() * 10 + 3,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$titlePrefix${reminder.title}")
            .setContentText(contentText)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(fullScreenPendingIntent, true) // ALWAYS attached for all reminders
            .addAction(0, "✓ Complete", completePendingIntent)
            .addAction(0, "💤 Snooze 15m", snoozePendingIntent)

        notificationManager.notify(reminder.id.toInt(), builder.build())
    }

    /**
     * Dismisses the old summary notification.
     * Individual pinned reminder notifications are posted directly instead of a generic summary.
     */
    fun updatePersistentNotifications(reminders: List<Reminder>) {
        notificationManager.cancel(SUMMARY_NOTIFICATION_ID)
    }

    fun showOfficeArrivalNotification(placeName: String, taskCount: Int) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            99901,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (taskCount > 0) "You have $taskCount office reminders waiting" else "You've arrived at $placeName. Tap to add a task."

        val notification = NotificationCompat.Builder(context, CHANNEL_OFFICE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏢 Arrived at $placeName")
            .setContentText(text)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(99901, notification)
    }

    fun showOfficeCheckInNotification(placeName: String) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            99902,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_OFFICE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏢 Office 1-Hour Check-in")
            .setContentText("You've been at $placeName for an hour — any quick tasks to log?")
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(99902, notification)
    }

    fun showPinnedNotification(reminder: Reminder) {
        val titleText = "📌 ${reminder.title}"
        val dueStr = SimpleDateFormat("EEE, MMM d • hh:mm a", Locale.getDefault()).format(Date(reminder.dueAt))

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            (reminder.id + 800000).toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_COMPLETE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (reminder.id + 800000).toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PERSISTENT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleText)
            .setContentText("Due $dueStr")
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(0, "✓ Complete", completePendingIntent)
            .build()

        notificationManager.notify((reminder.id + 800000).toInt(), notification)
    }

    fun cancelPinnedNotification(reminderId: Long) {
        notificationManager.cancel((reminderId + 800000).toInt())
    }

    fun cancelNotification(reminderId: Long) {
        notificationManager.cancel(reminderId.toInt())
        cancelPinnedNotification(reminderId)
        notificationManager.cancel(SUMMARY_NOTIFICATION_ID)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }
}
