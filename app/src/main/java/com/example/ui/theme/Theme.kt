package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color.Black,
    primaryContainer = CyanAccentDark,
    onPrimaryContainer = Color.White,
    secondary = VioletAccent,
    onSecondary = Color.White,
    background = Color.Transparent,
    surface = MenuSurface,
    onSurface = TextPrimary,
    surfaceVariant = MenuSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = MenuBorderSubtle
)

private val LightColorScheme = darkColorScheme( // Keep start menu sleek & dark glass by default
    primary = CyanAccent,
    onPrimary = Color.Black,
    surface = MenuSurface,
    onSurface = TextPrimary,
    surfaceVariant = MenuSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = MenuBorderSubtle
)

@Composable
fun PadStartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
