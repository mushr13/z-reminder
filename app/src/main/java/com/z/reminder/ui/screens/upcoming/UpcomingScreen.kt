package com.z.reminder.ui.screens.upcoming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.data.model.Reminder
import com.z.reminder.ui.components.ReminderCard
import com.z.reminder.ui.components.SectionHeader
import com.z.reminder.ui.screens.today.EmptyStateCard
import com.z.reminder.ui.theme.PrimaryViolet
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpcomingScreen(
    onBack: () -> Unit,
    onEditReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpcomingViewModel = koinViewModel()
) {
    val dayGroups by viewModel.groupedUpcomingReminders.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Upcoming Reminders",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (dayGroups.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                EmptyStateCard(
                    message = "No upcoming reminders scheduled",
                    subtext = "Tap + to plan tasks for future dates."
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                dayGroups.forEach { group ->
                    item {
                        SectionHeader(
                            title = group.dateLabel,
                            count = group.reminders.size,
                            badgeColor = PrimaryViolet
                        )
                    }

                    items(group.reminders, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onCompleteClick = { viewModel.completeReminder(reminder.id) },
                            onClick = { onEditReminder(reminder) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}
