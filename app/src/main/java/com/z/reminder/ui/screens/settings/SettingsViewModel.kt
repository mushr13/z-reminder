package com.z.reminder.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.z.reminder.data.db.PlaceDao
import com.z.reminder.data.model.Place
import com.z.reminder.data.model.PlaceType
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.data.repository.SettingsRepository
import com.z.reminder.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val placeDao: PlaceDao
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
}

