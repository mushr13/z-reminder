package com.z.reminder.ui.screens.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.repository.ReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UpcomingDayGroup(
    val dateLabel: String,
    val reminders: List<Reminder>
)

class UpcomingViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    val groupedUpcomingReminders: StateFlow<List<UpcomingDayGroup>> = repository.activeReminders
        .map { list ->
            val now = System.currentTimeMillis()
            val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
            list.filter { it.dueAt >= now }
                .groupBy { format.format(Date(it.dueAt)) }
                .map { (date, items) -> UpcomingDayGroup(date, items) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun completeReminder(id: Long) {
        viewModelScope.launch {
            repository.markCompleted(id)
        }
    }
}
