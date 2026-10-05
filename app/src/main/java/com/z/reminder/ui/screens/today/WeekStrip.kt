package com.z.reminder.ui.screens.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.z.reminder.ui.theme.PrimaryViolet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WeekStrip(
    isExpanded: Boolean,
    selectedCalendar: Calendar,
    onDaySelected: (Calendar) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        val today = Calendar.getInstance()
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayNumFormat = SimpleDateFormat("d", Locale.getDefault())

        // Generate current 7 days starting from 2 days ago to 4 days ahead
        val days = (0..6).map { offset ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = today.timeInMillis
                add(Calendar.DAY_OF_YEAR, offset - 1)
            }
            cal
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { dayCal ->
                val isToday = dayCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                        dayCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                val isSelected = dayCal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR) &&
                        dayCal.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR)

                val dayName = dayOfWeekFormat.format(Date(dayCal.timeInMillis))
                val dayNum = dayNumFormat.format(Date(dayCal.timeInMillis))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) PrimaryViolet
                            else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                            color = if (isToday && !isSelected) PrimaryViolet else Color.Transparent,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onDaySelected(dayCal) }
                        .padding(vertical = 10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = dayNum,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.SemiBold
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )

                        // Small status dot
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White.copy(alpha = 0.8f)
                                    else if (isToday) PrimaryViolet
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }
}
