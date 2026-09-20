package com.resq.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = RedPrimary,
    secondary = RedDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight
)

private val LightColors = lightColorScheme(
    primary = RedPrimary,
    secondary = RedDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun ResQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
