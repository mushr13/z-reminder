package com.z.reminder.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.data.model.Reminder
import com.z.reminder.ui.theme.CardShape
import com.z.reminder.ui.theme.OfficeBlue
import com.z.reminder.ui.theme.OverdueRed
import com.z.reminder.ui.theme.PrimaryViolet
import com.z.reminder.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderCard(
    reminder: Reminder,
    onCompleteClick: () -> Unit,
    onClick: () -> Unit,
    onDuplicateClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onSnoozeClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isOverdue: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }
    var showSnoozeSubmenu by remember { mutableStateOf(false) }

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(reminder.dueAt))

    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Rounded-Square Completion Checkbox (moderate 8dp radius conforming to rounded-rectangles)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (reminder.isCompleted) SuccessGreen
                        else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (reminder.isCompleted) SuccessGreen
                        else if (isOverdue) OverdueRed
                        else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onCompleteClick() }
            ) {
                if (reminder.isCompleted) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Reminder Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (reminder.isHighPriority) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OverdueRed)
                        )
                    }

                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (reminder.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (reminder.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Badges Row (Time, Repeat, Place, Snoozed count)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time chip
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (isOverdue && !reminder.isCompleted) OverdueRed else PrimaryViolet
                        )
                    )

                    // Repeat icon badge
                    if (reminder.hasRepeat) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Repeat,
                                contentDescription = "Repeats",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = reminder.repeatUnit ?: "",
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Place badge
                    if (reminder.placeId != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Place",
                                tint = OfficeBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Place",
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                color = OfficeBlue
                            )
                        }
                    }

                    // Snooze count indicator (flag if snoozed 3+ times)
                    if (reminder.snoozeCount > 0) {
                        Text(
                            text = "Snoozed ${reminder.snoozeCount}x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (reminder.snoozeCount >= 3) OverdueRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Quick Actions 3-dots Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = {
                        showMenu = false
                        showSnoozeSubmenu = false
                    },
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Rounded.Edit, null) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )

                    if (onSnoozeClick != null) {
                        DropdownMenuItem(
                            text = { Text("Snooze 10 min") },
                            leadingIcon = { Icon(Icons.Rounded.Snooze, null) },
                            onClick = {
                                showMenu = false
                                onSnoozeClick(10)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Snooze 1 hour") },
                            leadingIcon = { Icon(Icons.Rounded.Snooze, null) },
                            onClick = {
                                showMenu = false
                                onSnoozeClick(60)
                            }
                        )
                    }

                    if (onDuplicateClick != null) {
                        DropdownMenuItem(
                            text = { Text("Duplicate") },
                            leadingIcon = { Icon(Icons.Rounded.ContentCopy, null) },
                            onClick = {
                                showMenu = false
                                onDuplicateClick()
                            }
                        )
                    }

                    if (onDeleteClick != null) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = OverdueRed) },
                            leadingIcon = { Icon(Icons.Rounded.Delete, null, tint = OverdueRed) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }
        }
    }
}
