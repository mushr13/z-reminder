package com.z.reminder.ui.screens.addedit

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.data.model.QuickPreset
import com.z.reminder.data.model.Reminder
import com.z.reminder.data.model.ReminderPriority
import com.z.reminder.data.model.RepeatMode
import com.z.reminder.data.model.RepeatUnit
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
    onDelete: ((Reminder) -> Unit)? = null,
    existingReminder: Reminder? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    repository: ReminderRepository = koinInject()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var title by remember { mutableStateOf(existingReminder?.title ?: "") }
    var priority by remember { mutableStateOf(existingReminder?.priority ?: ReminderPriority.NORMAL.name) }
    var isPinned by remember { mutableStateOf(existingReminder?.isPinned ?: false) }

    val initialCal = remember {
        Calendar.getInstance().apply {
            if (existingReminder != null) {
                timeInMillis = existingReminder.dueAt
            } else {
                add(Calendar.HOUR_OF_DAY, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    var selectedCalendar by remember { mutableStateOf(initialCal) }
    var presetFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Repeat configuration
    var selectedRepeatUnit by remember {
        mutableStateOf(existingReminder?.repeatUnit?.let {
            try { RepeatUnit.valueOf(it) } catch (e: Exception) { null }
        })
    }
    var repeatInterval by remember { mutableIntStateOf(existingReminder?.repeatInterval ?: 1) }
    var repeatWeekdaysMask by remember { mutableIntStateOf(existingReminder?.repeatWeekdaysMask ?: 0) }
    var repeatMode by remember {
        mutableStateOf(existingReminder?.repeatMode?.let {
            try { RepeatMode.valueOf(it) } catch (e: Exception) { RepeatMode.FROM_DUE_TIME }
        } ?: RepeatMode.FROM_DUE_TIME)
    }

    // Custom repeat state
    var isCustomRepeat by remember {
        mutableStateOf(
            existingReminder?.repeatUnit == RepeatUnit.MINUTE.name ||
            existingReminder?.repeatUnit == RepeatUnit.HOUR.name
        )
    }
    var customHours by remember {
        mutableStateOf(
            if (existingReminder?.repeatUnit == RepeatUnit.HOUR.name) (existingReminder.repeatInterval).toString()
            else if (existingReminder?.repeatUnit == RepeatUnit.MINUTE.name && existingReminder.repeatInterval >= 60)
                (existingReminder.repeatInterval / 60).toString()
            else "0"
        )
    }
    var customMinutes by remember {
        mutableStateOf(
            if (existingReminder?.repeatUnit == RepeatUnit.MINUTE.name)
                (existingReminder.repeatInterval % 60).let { if (it == 0 && existingReminder.repeatInterval > 0) "0" else existingReminder.repeatInterval.toString() }
            else "30"
        )
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
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
                .verticalScroll(rememberScrollState())
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (existingReminder != null && onDelete != null) {
                        IconButton(onClick = { onDelete(existingReminder) }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete",
                                tint = OverdueRed
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

            Spacer(modifier = Modifier.height(16.dp))

            // Scheduled Date & Time Summary Card (Tap to pick date/time)
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
                    // Date Clicker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val updated = (selectedCalendar.clone() as Calendar).apply {
                                            set(Calendar.YEAR, year)
                                            set(Calendar.MONTH, month)
                                            set(Calendar.DAY_OF_MONTH, day)
                                        }
                                        selectedCalendar = updated
                                    },
                                    selectedCalendar.get(Calendar.YEAR),
                                    selectedCalendar.get(Calendar.MONTH),
                                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = "Date",
                            tint = PrimaryViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Time Clicker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val updated = (selectedCalendar.clone() as Calendar).apply {
                                            set(Calendar.HOUR_OF_DAY, hour)
                                            set(Calendar.MINUTE, minute)
                                        }
                                        selectedCalendar = updated
                                    },
                                    selectedCalendar.get(Calendar.HOUR_OF_DAY),
                                    selectedCalendar.get(Calendar.MINUTE),
                                    false
                                ).show()
                            }
                            .padding(4.dp)
                    ) {
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

            // Quick Presets
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
                                focusManager.clearFocus()
                                keyboardController?.hide()
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

            AnimatedVisibility(visible = presetFeedbackMessage != null) {
                presetFeedbackMessage?.let { msg ->
                    Text(
                        text = "✓ $msg",
                        style = MaterialTheme.typography.labelSmall.copy(color = PrimaryViolet),
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --- REPEAT ENGINE CONFIGURATION ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Repeat,
                        contentDescription = null,
                        tint = PrimaryViolet,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Repeat",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (selectedRepeatUnit != null || isCustomRepeat) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelMedium.copy(color = OverdueRed),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                selectedRepeatUnit = null
                                isCustomRepeat = false
                            }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Repeat Options: Daily, Weekly, Monthly, Custom (Tapping active deselects it)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val standardUnits = listOf(
                    RepeatUnit.DAY to "Daily",
                    RepeatUnit.WEEK to "Weekly",
                    RepeatUnit.MONTH to "Monthly"
                )

                standardUnits.forEach { (unit, label) ->
                    val isSelected = selectedRepeatUnit == unit && !isCustomRepeat
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(ChipShape)
                            .background(if (isSelected) PrimaryViolet else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                if (isSelected) {
                                    selectedRepeatUnit = null
                                    isCustomRepeat = false
                                } else {
                                    selectedRepeatUnit = unit
                                    isCustomRepeat = false
                                    repeatInterval = 1
                                }
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Custom Chip
                val isCustomSelected = isCustomRepeat
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(ChipShape)
                        .background(if (isCustomSelected) PrimaryViolet else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (isCustomSelected) {
                                isCustomRepeat = false
                                selectedRepeatUnit = null
                            } else {
                                isCustomRepeat = true
                                selectedRepeatUnit = RepeatUnit.MINUTE
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Custom",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isCustomSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Custom Repeat Input Box (Hours & Minutes)
            if (isCustomRepeat) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Repeat interval:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = customHours,
                            onValueChange = { customHours = it.filter { char -> char.isDigit() }.take(3) },
                            label = { Text("Hours") },
                            placeholder = { Text("0") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = customMinutes,
                            onValueChange = { customMinutes = it.filter { char -> char.isDigit() }.take(3) },
                            label = { Text("Minutes") },
                            placeholder = { Text("30") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // Quick custom presets
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "15m" to (0 to 15),
                            "30m" to (0 to 30),
                            "45m" to (0 to 45),
                            "1h" to (1 to 0),
                            "2h" to (2 to 0)
                        ).forEach { (label, pair) ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, PrimaryViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        customHours = pair.first.toString()
                                        customMinutes = pair.second.toString()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryViolet
                                )
                            }
                        }
                    }
                }
            }

            // Weekly Day Selector (If Weekly is selected)
            if (selectedRepeatUnit == RepeatUnit.WEEK && !isCustomRepeat) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Repeat on days:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                val dayNames = listOf(
                    1 to "M", 2 to "T", 3 to "W", 4 to "T", 5 to "F", 6 to "S", 7 to "S"
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dayNames.forEach { (dayVal, name) ->
                        val isDaySelected = (repeatWeekdaysMask and (1 shl dayVal)) != 0
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDaySelected) PrimaryViolet else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    repeatWeekdaysMask = if (isDaySelected) {
                                        repeatWeekdaysMask and (1 shl dayVal).inv()
                                    } else {
                                        repeatWeekdaysMask or (1 shl dayVal)
                                    }
                                }
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDaySelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Priority and PIN Toggles Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // PIN Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPinned) PrimaryViolet.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { isPinned = !isPinned }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryViolet)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = "Pin",
                        tint = if (isPinned) PrimaryViolet else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pin",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isPinned) PrimaryViolet else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Urgent Flag Pill
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
                        .padding(horizontal = 12.dp, vertical = 8.dp)
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

            Spacer(modifier = Modifier.height(26.dp))

            // Action Buttons (Cancel / Save with vibrant green subtle glow)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
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

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1.4f)
                ) {
                    // Soft green glowing aura behind Save button
                    if (title.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF10B981).copy(alpha = 0.40f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                focusManager.clearFocus()
                                keyboardController?.hide()

                                val finalRepeatUnit = if (isCustomRepeat) {
                                    RepeatUnit.MINUTE.name
                                } else {
                                    selectedRepeatUnit?.name
                                }
                                val finalRepeatInterval = if (isCustomRepeat) {
                                    val hrs = customHours.toIntOrNull() ?: 0
                                    val mins = customMinutes.toIntOrNull() ?: 0
                                    (hrs * 60 + mins).coerceAtLeast(1)
                                } else {
                                    repeatInterval
                                }

                                val reminder = Reminder(
                                    id = existingReminder?.id ?: 0,
                                    title = title.trim(),
                                    notes = "",
                                    dueAt = selectedCalendar.timeInMillis,
                                    repeatUnit = finalRepeatUnit,
                                    repeatInterval = finalRepeatInterval,
                                    repeatWeekdaysMask = repeatWeekdaysMask,
                                    repeatMode = repeatMode.name,
                                    priority = priority,
                                    isPinned = isPinned,
                                    nagIntervalMinutes = 30
                                )
                                onSave(reminder)
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = ButtonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981), // Vibrant Green
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (existingReminder != null) "Update" else "Save Reminder",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
