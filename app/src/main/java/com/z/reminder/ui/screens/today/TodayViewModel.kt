package com.z.reminder.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class TodayUiState(
    val overdue: List<Reminder> = emptyList(),
    val today: List<Reminder> = emptyList(),
    val tomorrow: List<Reminder> = emptyList(),
    val later: List<Reminder> = emptyList(),
    val isOverdueExpanded: Boolean = true,
    val isLaterExpanded: Boolean = false,
    val isWeekStripExpanded: Boolean = true,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class TodayViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _isOverdueExpanded = MutableStateFlow(true)
    private val _isLaterExpanded = MutableStateFlow(false)
    private val _isWeekStripExpanded = MutableStateFlow(true)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TodayUiState> = combine(
        repository.activeReminders,
        _isOverdueExpanded,
        _isLaterExpanded,
        _isWeekStripExpanded,
        _searchQuery
    ) { reminders, overdueExpanded, laterExpanded, weekStripExpanded, query ->
        val now = System.currentTimeMillis()
        
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val tomorrowStart = Calendar.getInstance().apply {
            timeInMillis = todayStart
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val dayAfterTomorrowStart = Calendar.getInstance().apply {
            timeInMillis = tomorrowStart
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val filtered = if (query.isBlank()) {
            reminders
        } else {
            reminders.filter { it.title.contains(query, ignoreCase = true) || it.notes.contains(query, ignoreCase = true) }
        }

        val overdueList = filtered.filter { it.dueAt < now }
        val todayList = filtered.filter { it.dueAt in now until tomorrowStart }
        val tomorrowList = filtered.filter { it.dueAt in tomorrowStart until dayAfterTomorrowStart }
        val laterList = filtered.filter { it.dueAt >= dayAfterTomorrowStart }

        TodayUiState(
            overdue = overdueList,
            today = todayList,
            tomorrow = tomorrowList,
            later = laterList,
            isOverdueExpanded = overdueExpanded,
            isLaterExpanded = laterExpanded,
            isWeekStripExpanded = weekStripExpanded,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayUiState(isLoading = true)
    )

    fun completeReminder(id: Long) {
        viewModelScope.launch {
            repository.markCompleted(id)
        }
    }

    fun snoozeReminder(id: Long, minutes: Int = 10) {
        viewModelScope.launch {
            val snoozeUntil = System.currentTimeMillis() + (minutes * 60 * 1000L)
            repository.snoozeReminder(id, snoozeUntil)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun duplicateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.duplicateReminder(reminder)
        }
    }

    fun toggleOverdue() {
        _isOverdueExpanded.value = !_isOverdueExpanded.value
    }

    fun toggleLater() {
        _isLaterExpanded.value = !_isLaterExpanded.value
    }

    fun toggleWeekStrip() {
        _isWeekStripExpanded.value = !_isWeekStripExpanded.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
