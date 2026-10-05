package com.z.reminder.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}

private val LightColorScheme = lightColorScheme(
    primary = PrimaryViolet,
    onPrimary = TextWhite,
    primaryContainer = SecondaryViolet,
    onPrimaryContainer = TextNavy,
    background = LavenderBg,
    onBackground = TextNavy,
    surface = LavenderCard,
    onSurface = TextNavy,
    surfaceVariant = BorderSubtle,
    onSurfaceVariant = TextSubtle,
    outline = BorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = TextWhite,
    primaryContainer = DarkCardElevated,
    onPrimaryContainer = TextWhite,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkCard,
    onSurface = TextWhite,
    surfaceVariant = DarkCardElevated,
    onSurfaceVariant = TextSubtleDark,
    outline = BorderSubtleDark
)

private val AmoledColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = TextWhite,
    primaryContainer = AmoledCardElevated,
    onPrimaryContainer = TextWhite,
    background = AmoledBg,
    onBackground = TextWhite,
    surface = AmoledCard,
    onSurface = TextWhite,
    surfaceVariant = AmoledCardElevated,
    onSurfaceVariant = TextSubtleDark,
    outline = BorderSubtleAmoled
)

@Composable
fun ZReminderTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK, AppThemeMode.AMOLED -> true
    }

    val colorScheme = when {
        themeMode == AppThemeMode.AMOLED -> AmoledColorScheme
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ZShapes,
        content = content
    )
}
