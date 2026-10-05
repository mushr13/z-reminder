package com.z.reminder.permission

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat

data class PermissionStatus(
    val id: String,
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val isCritical: Boolean = true,
    val intentAction: () -> Intent
)

class PermissionHelper(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun getPermissionStatuses(): List<PermissionStatus> {
        val list = mutableListOf<PermissionStatus>()

        // 1. Exact Alarms
        val canExactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
        list.add(
            PermissionStatus(
                id = "exact_alarm",
                title = "Exact Alarms",
                description = "Required for ringing at the precise minute without delay.",
                isGranted = canExactAlarm,
                isCritical = true,
                intentAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    } else {
                        appDetailsIntent()
                    }
                }
            )
        )

        // 2. Notifications
        val notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }
        list.add(
            PermissionStatus(
                id = "notifications",
                title = "Post Notifications",
                description = "Shows urgent alerts, ongoing task shade, and office prompts.",
                isGranted = notifGranted,
                isCritical = true,
                intentAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    } else {
                        appDetailsIntent()
                    }
                }
            )
        )

        // 3. Battery Optimization Exemption
        val isIgnoringBattery = powerManager.isIgnoringBatteryOptimizations(context.packageName)
        list.add(
            PermissionStatus(
                id = "battery_opt",
                title = "Battery Unrestricted",
                description = "Prevents MagicOS from killing alarms and background timers in sleep mode.",
                isGranted = isIgnoringBattery,
                isCritical = true,
                intentAction = {
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
            )
        )

        // 4. Full-Screen Intent (Lockscreen Display)
        val canFullScreen = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            notificationManager.canUseFullScreenIntent()
        } else {
            true
        }
        list.add(
            PermissionStatus(
                id = "full_screen",
                title = "Display over Lockscreen",
                description = "Wakes up screen and shows the full alert card when ringing.",
                isGranted = canFullScreen,
                isCritical = true,
                intentAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                            data = Uri.parse("package:${context.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    } else {
                        appDetailsIntent()
                    }
                }
            )
        )

        // 5. Honor MagicOS Auto-Launch (App Launch Manager)
        list.add(
            PermissionStatus(
                id = "honor_autolaunch",
                title = "MagicOS Auto-Launch Guide",
                description = "Set 'App Launch' to 'Manage Manually' (Enable Auto-launch, Secondary launch & Background).",
                isGranted = true, // Informational / Deep link
                isCritical = false,
                intentAction = {
                    // Try Honor/Huawei Startup Manager, or fallback to app settings
                    val honorIntent = Intent().apply {
                        setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (isIntentAvailable(honorIntent)) {
                        honorIntent
                    } else {
                        appDetailsIntent()
                    }
                }
            )
        )

        return list
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    }

    private fun appDetailsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}
