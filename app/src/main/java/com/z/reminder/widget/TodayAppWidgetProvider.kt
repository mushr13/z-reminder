package com.z.reminder.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.z.reminder.MainActivity
import com.z.reminder.R
import com.z.reminder.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TodayAppWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val database: AppDatabase by inject()

    companion object {
        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, TodayAppWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, TodayAppWidgetProvider::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startOfDay = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val endOfDay = cal.timeInMillis

                val todayReminders = database.reminderDao()
                    .getRemindersForDay(startOfDay, endOfDay)
                    .firstOrNull() ?: emptyList()

                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

                for (appWidgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.widget_today)

                    // Click root -> Open App
                    val appIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val appPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        appIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

                    // Click Add button -> Open Quick Add Sheet
                    val addIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("extra_action_add_reminder", true)
                    }
                    val addPendingIntent = PendingIntent.getActivity(
                        context,
                        9991,
                        addIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_btn_add, addPendingIntent)

                    // Count Badge
                    val count = todayReminders.size
                    views.setTextViewText(R.id.widget_badge, "$count Due")

                    // Fill items
                    if (todayReminders.isEmpty()) {
                        views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_item_1, View.GONE)
                        views.setViewVisibility(R.id.widget_item_2, View.GONE)
                        views.setViewVisibility(R.id.widget_item_3, View.GONE)
                    } else {
                        views.setViewVisibility(R.id.widget_empty_text, View.GONE)

                        val itemIds = listOf(R.id.widget_item_1, R.id.widget_item_2, R.id.widget_item_3)
                        for (i in 0 until 3) {
                            if (i < todayReminders.size) {
                                val rem = todayReminders[i]
                                val timeStr = timeFormat.format(Date(rem.dueAt))
                                views.setViewVisibility(itemIds[i], View.VISIBLE)
                                views.setTextViewText(itemIds[i], "• $timeStr  ${rem.title}")
                            } else {
                                views.setViewVisibility(itemIds[i], View.GONE)
                            }
                        }
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
