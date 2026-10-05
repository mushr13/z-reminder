package com.z.reminder.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ZShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Specific component shapes conforming to rounded-rectangle discipline
val CardShape = RoundedCornerShape(24.dp)
val DialogShape = RoundedCornerShape(24.dp)
val ButtonShape = RoundedCornerShape(14.dp)
val ChipShape = RoundedCornerShape(12.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
val RaisedFabShape = RoundedCornerShape(18.dp)
