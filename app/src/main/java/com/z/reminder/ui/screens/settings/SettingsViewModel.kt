package com.z.reminder.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.data.repository.SettingsRepository
import com.z.reminder.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
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
}
