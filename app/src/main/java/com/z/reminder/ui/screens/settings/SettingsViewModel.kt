package com.z.reminder.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.z.reminder.alarm.AlarmScheduler
import com.z.reminder.data.db.PlaceDao
import com.z.reminder.data.db.ReminderDao
import com.z.reminder.data.model.AlertStyle
import com.z.reminder.data.model.Place
import com.z.reminder.data.model.PlaceType
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.model.ReminderPriority
import com.z.reminder.data.repository.ReminderRepository
import com.z.reminder.data.repository.SettingsRepository
import com.z.reminder.telegram.TelegramNotifier
import com.z.reminder.permission.PermissionHelper
import com.z.reminder.permission.PermissionStatus
import com.z.reminder.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val placeDao: PlaceDao,
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler,
    private val permissionHelper: PermissionHelper,
    private val reminderRepository: ReminderRepository,
    private val telegramNotifier: TelegramNotifier
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = settingsRepository.themeModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeMode.SYSTEM
        )

    val nagInterval: StateFlow<Int> = settingsRepository.nagIntervalFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 30
        )

    val telegramEnabled: StateFlow<Boolean> = settingsRepository.telegramEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val telegramBotToken: StateFlow<String> = settingsRepository.telegramBotTokenFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsRepository.DEFAULT_BOT_TOKEN
        )

    val telegramChatId: StateFlow<String> = settingsRepository.telegramChatIdFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsRepository.DEFAULT_CHAT_ID
        )

    val badgesEnabled: StateFlow<Boolean> = settingsRepository.badgesEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setBadgesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBadgesEnabled(enabled)
        }
    }

    fun setTelegramEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setTelegramEnabled(enabled)
        }
    }

    fun saveTelegramConfig(token: String, chatId: String) {
        viewModelScope.launch {
            settingsRepository.setTelegramBotToken(token.trim())
            settingsRepository.setTelegramChatId(chatId.trim())
        }
    }

    fun sendTestTelegramMessage(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = telegramNotifier.sendMessage(
                "✅ *Z Reminder Connected to Office Bot!*\n\n" +
                "Test notification received successfully from your Honor phone.\n" +
                "Whenever you set a reminder, you'll get a ping here too!"
            )
            if (result.isSuccess) {
                onResult(true, "Test notification sent to Office Bot successfully!")
            } else {
                val err = result.exceptionOrNull()?.message ?: "Unknown error"
                onResult(false, "Failed: $err")
            }
        }
    }

    val places: StateFlow<List<Place>> = placeDao.getAllPlaces()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setNagInterval(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setNagInterval(minutes)
        }
    }

    fun getQuickPresets(): List<QuickPreset> {
        return settingsRepository.getQuickPresets()
    }

    fun getPermissions(): List<PermissionStatus> {
        return permissionHelper.getPermissionStatuses()
    }

    fun scheduleTestAlarmInOneMinute(onScheduled: () -> Unit) {
        viewModelScope.launch {
            val dueAt = System.currentTimeMillis() + 60 * 1000L
            val testReminder = Reminder(
                title = "🔔 Live 1-Minute Alarm Test",
                notes = "Lock your screen now to verify full-screen wake up, audio ramp, and vibration!",
                dueAt = dueAt,
                priority = ReminderPriority.HIGH.name,
                alertStyle = AlertStyle.FULL_SCREEN.name,
                backgroundId = "lavender_dream"
            )
            val id = reminderDao.insertReminder(testReminder)
            alarmScheduler.scheduleExactAlarm(testReminder.copy(id = id))
            onScheduled()
        }
    }

    fun saveOfficeWifi(wifiSsids: String) {
        viewModelScope.launch {
            val all = placeDao.getAllPlaces().firstOrNull() ?: emptyList()
            val existing = all.firstOrNull { it.type == PlaceType.OFFICE }
            if (existing != null) {
                placeDao.updatePlace(existing.copy(wifiSsids = wifiSsids.trim()))
            } else {
                placeDao.insertPlace(
                    Place(
                        name = "Office",
                        wifiSsids = wifiSsids.trim(),
                        type = PlaceType.OFFICE,
                        isTrackingEnabled = true
                    )
                )
            }
        }
    }

    fun exportBackup(onExported: (String) -> Unit) {
        viewModelScope.launch {
            val json = reminderRepository.exportRemindersJson()
            onExported(json)
        }
    }

    fun importBackup(jsonStr: String, onImported: (Int) -> Unit) {
        viewModelScope.launch {
            val count = reminderRepository.importRemindersJson(jsonStr)
            onImported(count)
        }
    }
}
