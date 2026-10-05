package com.z.reminder.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.ui.theme.PrimaryViolet
import com.z.reminder.ui.theme.RaisedFabShape

@Composable
fun ZBottomNav(
    currentRoute: String,
    completedCount: Int,
    upcomingCount: Int,
    badgesEnabled: Boolean,
    onNavigateToToday: () -> Unit,
    onNavigateToCompleted: () -> Unit,
    onNavigateToUpcoming: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom bar surface with rounded top corners
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // LEFT BUTTON: Completed (with dynamic badge)
                val isCompletedActive = currentRoute == Screen.Completed.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (isCompletedActive) onNavigateToToday() else onNavigateToCompleted()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircleOutline,
                            contentDescription = "Completed History",
                            tint = if (isCompletedActive) PrimaryViolet else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        if (badgesEnabled && completedCount > 0) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-4).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryViolet)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (completedCount > 99) "99+" else completedCount.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isCompletedActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isCompletedActive) PrimaryViolet else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Spacer for the center raised button
                Spacer(modifier = Modifier.size(60.dp))

                // RIGHT BUTTON: Upcoming (with dynamic badge)
                val isUpcomingActive = currentRoute == Screen.Upcoming.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (isUpcomingActive) onNavigateToToday() else onNavigateToUpcoming()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = "Upcoming",
                            tint = if (isUpcomingActive) PrimaryViolet else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        if (badgesEnabled && upcomingCount > 0) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-4).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryViolet)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (upcomingCount > 99) "99+" else upcomingCount.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Upcoming",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isUpcomingActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isUpcomingActive) PrimaryViolet else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // CENTER BUTTON: Large raised floating Add button with glowing aura
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.offset(y = (-20).dp)
        ) {
            // Subtle glowing aura behind the raised button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PrimaryViolet.copy(alpha = 0.40f),
                                PrimaryViolet.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            FloatingActionButton(
                onClick = onAddClick,
                shape = RaisedFabShape,
                containerColor = PrimaryViolet,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Reminder",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
