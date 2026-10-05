package com.z.reminder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.z.reminder.ui.theme.PrimaryViolet

@Composable
fun SectionHeader(
    title: String,
    count: Int,
    isExpanded: Boolean? = null,
    onToggleExpand: (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    badgeColor: Color = PrimaryViolet,
    modifier: Modifier = Modifier
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded == true) 180f else 0f,
        label = "arrow_rotation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (onToggleExpand != null) Modifier.clickable { onToggleExpand() }
                else Modifier
            )
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = titleColor
            )

            if (count > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Badge(
                    containerColor = badgeColor,
                    contentColor = Color.White
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        if (onToggleExpand != null && isExpanded != null) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(22.dp)
                    .rotate(rotationState)
            )
        }
    }
}
