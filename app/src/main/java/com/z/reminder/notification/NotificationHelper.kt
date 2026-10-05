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

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_URGENT = "z_reminder_urgent"
        const val CHANNEL_NAG = "z_reminder_nag"
        const val CHANNEL_PERSISTENT = "z_reminder_persistent"
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
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // 1. Urgent Reminders Channel (High Importance, Alarm sound, Vibration)
            val urgentChannel = NotificationChannel(
                CHANNEL_URGENT,
                "Urgent Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarms and full alert notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 2. Nagging Alerts Channel (Medium-High Importance)
            val nagChannel = NotificationChannel(
                CHANNEL_NAG,
                "Nagging Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Follow-up nagging alerts until a task is completed"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 3. Persistent Notification Shade (Low Importance, Silent)
            val persistentChannel = NotificationChannel(
                CHANNEL_PERSISTENT,
                "Active Tasks Shade",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing summary of today's pending reminders"
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
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
     * Shows a firing reminder notification with Complete and Snooze action buttons.
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

        // Snooze 10m Action Intent
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_SNOOZE_MINUTES, 10)
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
            .addAction(0, "✓ Complete", completePendingIntent)
            .addAction(0, "💤 Snooze 10m", snoozePendingIntent)

        if (reminder.alertStyle == com.z.reminder.data.model.AlertStyle.FULL_SCREEN.name ||
            reminder.priority == com.z.reminder.data.model.ReminderPriority.HIGH.name) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        notificationManager.notify(reminder.id.toInt(), builder.build())
    }

    /**
     * Updates the persistent ongoing notifications for today's active & overdue tasks.
     * Attaches deleteIntent so if swiped away, it automatically re-posts unless completed!
     */
    fun updatePersistentNotifications(reminders: List<Reminder>) {
        if (reminders.isEmpty()) {
            notificationManager.cancel(SUMMARY_NOTIFICATION_ID)
            return
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val repostIntent = Intent(context, NotificationDismissReceiver::class.java).apply {
            action = ACTION_REPOST_PERSISTENT
        }
        val repostPendingIntent = PendingIntent.getBroadcast(
            context,
            9998,
            repostIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val inboxStyle = NotificationCompat.InboxStyle()
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        reminders.take(6).forEach { rem ->
            val time = timeFormat.format(Date(rem.dueAt))
            inboxStyle.addLine("$time - ${rem.title}")
        }

        val count = reminders.size
        val summaryText = if (count == 1) "1 pending reminder" else "$count pending reminders"

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_PERSISTENT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Z Reminder: $summaryText")
            .setContentText("Tap to open your daily tasks")
            .setStyle(inboxStyle)
            .setContentIntent(openPendingIntent)
            .setDeleteIntent(repostPendingIntent) // Re-post if swiped
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
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

    fun cancelNotification(reminderId: Long) {
        notificationManager.cancel(reminderId.toInt())
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }
}
