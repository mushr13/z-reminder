package com.z.reminder.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "z_reminder_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        const val DEFAULT_BOT_TOKEN = "8830465318:AAG5Zt2ydJ2HeX0ZiYMxBz7viuhE4O5MxbE"
        const val DEFAULT_CHAT_ID = "8735336077"
    }

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NAG_INTERVAL_MINUTES = intPreferencesKey("nag_interval_minutes")
        val QUIET_HOURS_START = intPreferencesKey("quiet_hours_start") // e.g. 23 (11 PM)
        val QUIET_HOURS_END = intPreferencesKey("quiet_hours_end")     // e.g. 6 (6 AM)
        val OFFICE_START_HOUR = intPreferencesKey("office_start_hour") // e.g. 9
        val OFFICE_END_HOUR = intPreferencesKey("office_end_hour")     // e.g. 18
        val TELEGRAM_ENABLED = booleanPreferencesKey("telegram_enabled")
        val TELEGRAM_BOT_TOKEN = stringPreferencesKey("telegram_bot_token")
        val TELEGRAM_CHAT_ID = stringPreferencesKey("telegram_chat_id")
        val BADGES_ENABLED = booleanPreferencesKey("badges_enabled")
    }

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data.map { prefs ->
        val name = prefs[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        try {
            AppThemeMode.valueOf(name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    val nagIntervalFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.NAG_INTERVAL_MINUTES] ?: 30
    }

    suspend fun setNagInterval(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NAG_INTERVAL_MINUTES] = minutes
        }
    }

    val telegramEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.TELEGRAM_ENABLED] ?: true
    }

    suspend fun setTelegramEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.TELEGRAM_ENABLED] = enabled
        }
    }

    val telegramBotTokenFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.TELEGRAM_BOT_TOKEN] ?: DEFAULT_BOT_TOKEN
    }

    suspend fun setTelegramBotToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.TELEGRAM_BOT_TOKEN] = token
        }
    }

    val telegramChatIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.TELEGRAM_CHAT_ID] ?: DEFAULT_CHAT_ID
    }

    suspend fun setTelegramChatId(chatId: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.TELEGRAM_CHAT_ID] = chatId
        }
    }

    val badgesEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.BADGES_ENABLED] ?: true
    }

    suspend fun setBadgesEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.BADGES_ENABLED] = enabled
        }
    }

    // Default 4 Quick Presets
    fun getQuickPresets(): List<QuickPreset> {
        return QuickPreset.defaultPresets()
    }
}
