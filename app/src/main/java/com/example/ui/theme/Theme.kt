package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JarvisColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = DarkGlassSurface,
    onPrimaryContainer = TextCyanGlow,
    secondary = NeonBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = DarkGlassBorder,
    onSecondaryContainer = TextCyanGlow,
    tertiary = HologramAmber,
    onTertiary = Color.Black,
    background = DeepSpaceBackground,
    onBackground = TextCyanGlow,
    surface = DarkGlassSurface,
    onSurface = TextCyanGlow,
    surfaceVariant = DarkGlassBorder,
    onSurfaceVariant = TextMuted,
    outline = CyanPrimaryVariant,
    error = CriticalRed
)

@Composable
fun JarvisTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for backwards compatibility with starter template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    JarvisTheme(content = content)
}
