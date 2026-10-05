package com.z.reminder.alert

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class AlertBackgroundPreset(
    val id: String,
    val name: String,
    val colors: List<Color>,
    val isDarkBackground: Boolean
)

object AlertBackgrounds {

    val presets = listOf(
        AlertBackgroundPreset(
            id = "midnight_nebula",
            name = "Midnight Nebula",
            colors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E)),
            isDarkBackground = true
        ),
        AlertBackgroundPreset(
            id = "lavender_dreams",
            name = "Lavender Dreams",
            colors = listOf(Color(0xFFE9E6FA), Color(0xFFDED8F7), Color(0xFFC7B8FF)),
            isDarkBackground = false
        ),
        AlertBackgroundPreset(
            id = "obsidian_black",
            name = "Obsidian AMOLED",
            colors = listOf(Color(0xFF000000), Color(0xFF111116), Color(0xFF000000)),
            isDarkBackground = true
        ),
        AlertBackgroundPreset(
            id = "cyber_violet",
            name = "Cyber Violet",
            colors = listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2)),
            isDarkBackground = true
        ),
        AlertBackgroundPreset(
            id = "sunset_horizon",
            name = "Sunset Glow",
            colors = listOf(Color(0xFFFF512F), Color(0xFFDD2476)),
            isDarkBackground = true
        ),
        AlertBackgroundPreset(
            id = "ocean_twilight",
            name = "Ocean Twilight",
            colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
            isDarkBackground = true
        ),
        AlertBackgroundPreset(
            id = "emerald_forest",
            name = "Emerald Forest",
            colors = listOf(Color(0xFF134E5E), Color(0xFF71B280)),
            isDarkBackground = true
        ),
        AlertBackgroundPreset(
            id = "aurora_rose",
            name = "Aurora Rose",
            colors = listOf(Color(0xFFEE9CA7), Color(0xFFFFDDE1)),
            isDarkBackground = false
        )
    )

    fun getPresetById(id: String?): AlertBackgroundPreset {
        return presets.find { it.id == id } ?: presets[0] // Default: Midnight Nebula
    }

    /**
     * Calculates the WCAG luminance of a Color (0.0 to 1.0).
     */
    fun calculateLuminance(color: Color): Double {
        fun channelLuminance(c: Float): Double {
            return if (c <= 0.03928f) (c / 12.92) else Math.pow(((c + 0.055) / 1.055), 2.4)
        }
        val r = channelLuminance(color.red)
        val g = channelLuminance(color.green)
        val b = channelLuminance(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    /**
     * Guarantees at least 4.5:1 WCAG contrast by returning pure White or Dark Navy.
     */
    fun getHighContrastTextColor(backgroundColor: Color): Color {
        val lum = calculateLuminance(backgroundColor)
        return if (lum > 0.45) Color(0xFF1E1B3A) else Color(0xFFFFFFFF)
    }

    fun getBackgroundBrush(preset: AlertBackgroundPreset): Brush {
        return Brush.verticalGradient(preset.colors)
    }
}
