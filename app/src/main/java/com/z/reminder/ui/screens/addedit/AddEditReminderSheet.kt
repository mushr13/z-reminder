package com.z.reminder.ui.screens.addedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.model.ReminderPriority
import com.z.reminder.data.repository.ReminderRepository
import com.z.reminder.ui.theme.BottomSheetShape
import com.z.reminder.ui.theme.ButtonShape
import com.z.reminder.ui.theme.ChipShape
import com.z.reminder.ui.theme.OverdueRed
import com.z.reminder.ui.theme.PrimaryViolet
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderSheet(
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit,
    existingReminder: Reminder? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    repository: ReminderRepository = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf(existingReminder?.title ?: "") }
    var notes by remember { mutableStateOf(existingReminder?.notes ?: "") }
    var priority by remember { mutableStateOf(existingReminder?.priority ?: ReminderPriority.NORMAL.name) }

    val initialCal = remember {
        Calendar.getInstance().apply {
            if (existingReminder != null) {
                timeInMillis = existingReminder.dueAt
            } else {
                // Default: 1 hour from now
                add(Calendar.HOUR_OF_DAY, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    var selectedCalendar by remember { mutableStateOf(initialCal) }
    var presetFeedbackMessage by remember { mutableStateOf<String?>(null) }

    val timePickerState = rememberTimePickerState(
        initialHour = selectedCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = selectedCalendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    // Sync time picker changes to calendar
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        val updated = (selectedCalendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
            set(Calendar.MINUTE, timePickerState.minute)
        }
        selectedCalendar = updated
    }

    val quickPresets = remember { QuickPreset.defaultPresets() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetShape,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (existingReminder != null) "Edit Reminder" else "New Reminder",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What do you need to do?") },
                placeholder = { Text("e.g., Call office, submit report...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryViolet,
                    focusedLabelColor = PrimaryViolet
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notes input
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                placeholder = { Text("Add details or links...") },
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryViolet,
                    focusedLabelColor = PrimaryViolet
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scheduled Date & Time Summary Card
            val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val dateStr = dateFormat.format(Date(selectedCalendar.timeInMillis))
            val timeStr = timeFormat.format(Date(selectedCalendar.timeInMillis))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = "Date",
                            tint = PrimaryViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AccessTime,
                            contentDescription = "Time",
                            tint = PrimaryViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryViolet
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- 4 QUICK PRESET CHIPS (Right below time, with smart auto-spacing!) ---
            Text(
                text = "Quick Time Presets",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickPresets) { preset ->
                    Box(
                        modifier = Modifier
                            .clip(ChipShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, PrimaryViolet.copy(alpha = 0.5f), ChipShape)
                            .clickable {
                                coroutineScope.launch {
                                    val targetMillis = repository.calculatePresetTargetTime(preset)
                                    val updatedCal = Calendar.getInstance().apply {
                                        timeInMillis = targetMillis
                                    }
                                    selectedCalendar = updatedCal
                                    val formattedTarget = timeFormat.format(Date(targetMillis))
                                    presetFeedbackMessage = "Set to $formattedTarget (${preset.title})"
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = preset.title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = PrimaryViolet
                        )
                    }
                }
            }

            // Staggering feedback message
            AnimatedVisibility(visible = presetFeedbackMessage != null) {
                presetFeedbackMessage?.let { msg ->
                    Text(
                        text = "✓ $msg",
                        style = MaterialTheme.typography.labelSmall.copy(color = PrimaryViolet),
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "High Priority Alert",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (priority == ReminderPriority.HIGH.name) OverdueRed.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            priority = if (priority == ReminderPriority.HIGH.name) {
                                ReminderPriority.NORMAL.name
                            } else {
                                ReminderPriority.HIGH.name
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Flag,
                            contentDescription = "Priority",
                            tint = if (priority == ReminderPriority.HIGH.name) OverdueRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (priority == ReminderPriority.HIGH.name) "Urgent" else "Normal",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (priority == ReminderPriority.HIGH.name) OverdueRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons (Cancel / Save)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = ButtonShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val reminder = Reminder(
                                id = existingReminder?.id ?: 0,
                                title = title.trim(),
                                notes = notes.trim(),
                                dueAt = selectedCalendar.timeInMillis,
                                priority = priority,
                                nagIntervalMinutes = 30
                            )
                            onSave(reminder)
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (existingReminder != null) "Update" else "Save Reminder",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
