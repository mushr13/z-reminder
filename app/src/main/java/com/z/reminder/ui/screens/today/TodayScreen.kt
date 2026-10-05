package com.z.reminder.ui.screens.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.data.model.Reminder
import com.z.reminder.ui.components.ReminderCard
import com.z.reminder.ui.components.SectionHeader
import com.z.reminder.ui.theme.CardShape
import com.z.reminder.ui.theme.OverdueBg
import com.z.reminder.ui.theme.OverdueRed
import com.z.reminder.ui.theme.PrimaryViolet
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    onNavigateToSettings: () -> Unit,
    onEditReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    val todayFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val todayDateString = todayFormat.format(Date())

    // Greeting calculation
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Hello"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // --- Top Bar (Greeting, Date, Search & Settings) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "$greeting ✨",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayDateString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Rounded.Close else Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { viewModel.toggleWeekStrip() }) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarViewWeek,
                            contentDescription = "Toggle Week Strip",
                            tint = if (uiState.isWeekStripExpanded) PrimaryViolet else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar (Animated visibility)
            AnimatedVisibility(visible = isSearchActive) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search your reminders...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }

            // Collapsible Week Strip
            WeekStrip(
                isExpanded = uiState.isWeekStripExpanded,
                selectedCalendar = selectedCalendar,
                onDaySelected = { selectedCalendar = it },
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // --- Main Content: Overdue, Today, Tomorrow, Later ---
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. OVERDUE SECTION (If any items are overdue)
            if (uiState.overdue.isNotEmpty()) {
                item {
                    Card(
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = OverdueBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SectionHeader(
                                title = "Overdue",
                                count = uiState.overdue.size,
                                isExpanded = uiState.isOverdueExpanded,
                                onToggleExpand = { viewModel.toggleOverdue() },
                                titleColor = OverdueRed,
                                badgeColor = OverdueRed
                            )

                            AnimatedVisibility(visible = uiState.isOverdueExpanded) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    uiState.overdue.forEach { reminder ->
                                        ReminderCard(
                                            reminder = reminder,
                                            onCompleteClick = { viewModel.completeReminder(reminder.id) },
                                            onClick = { onEditReminder(reminder) },
                                            isOverdue = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. TODAY SECTION
            item {
                SectionHeader(
                    title = "Today",
                    count = uiState.today.size,
                    badgeColor = PrimaryViolet
                )
            }

            if (uiState.today.isEmpty()) {
                item {
                    EmptyStateCard(
                        message = "All done for today! Enjoy your free time.",
                        subtext = "Tap + below to add a reminder."
                    )
                }
            } else {
                items(uiState.today, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onCompleteClick = { viewModel.completeReminder(reminder.id) },
                        onClick = { onEditReminder(reminder) }
                    )
                }
            }

            // 3. TOMORROW SECTION
            if (uiState.tomorrow.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Tomorrow",
                        count = uiState.tomorrow.size,
                        badgeColor = PrimaryViolet.copy(alpha = 0.8f)
                    )
                }

                items(uiState.tomorrow, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onCompleteClick = { viewModel.completeReminder(reminder.id) },
                        onClick = { onEditReminder(reminder) }
                    )
                }
            }

            // 4. LATER SECTION (Collapsible)
            if (uiState.later.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Later",
                        count = uiState.later.size,
                        isExpanded = uiState.isLaterExpanded,
                        onToggleExpand = { viewModel.toggleLater() },
                        badgeColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.isLaterExpanded) {
                    items(uiState.later, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onCompleteClick = { viewModel.completeReminder(reminder.id) },
                            onClick = { onEditReminder(reminder) }
                        )
                    }
                }
            }

            // Bottom spacing to avoid floating action button overlap
            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(vertical = 32.dp, horizontal = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = PrimaryViolet.copy(alpha = 0.6f),
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
