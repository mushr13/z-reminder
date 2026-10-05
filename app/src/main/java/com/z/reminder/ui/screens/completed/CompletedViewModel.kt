package com.z.reminder.ui.screens.completed

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

data class CompletedDayGroup(
    val dateLabel: String,
    val reminders: List<Reminder>
)

class CompletedViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    val groupedCompletedReminders: StateFlow<List<CompletedDayGroup>> = repository.completedReminders
        .map { list ->
            val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
            list.groupBy { format.format(Date(it.completedAt ?: it.dueAt)) }
                .map { (date, items) -> CompletedDayGroup(date, items) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun restoreReminder(id: Long) {
        viewModelScope.launch {
            repository.restoreCompleted(id)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }
}
