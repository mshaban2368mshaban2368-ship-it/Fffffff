package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FaTealGreen,
    onPrimary = Color.White,
    primaryContainer = FaDarkOutgoingBubble,
    onPrimaryContainer = FaDarkTextPrimary,
    secondary = FaGreenAccent,
    onSecondary = Color.Black,
    background = FaDarkBg,
    onBackground = FaDarkTextPrimary,
    surface = FaDarkSurface,
    onSurface = FaDarkTextPrimary,
    surfaceVariant = FaDarkIncomingBubble,
    onSurfaceVariant = FaDarkTextSecondary,
    outline = FaDarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = FaTealDark,
    onPrimary = Color.White,
    primaryContainer = FaLightOutgoingBubble,
    onPrimaryContainer = FaLightTextPrimary,
    secondary = FaGreenAccent,
    onSecondary = Color.White,
    background = FaLightBg,
    onBackground = FaLightTextPrimary,
    surface = FaLightSurface,
    onSurface = FaLightTextPrimary,
    surfaceVariant = FaLightIncomingBubble,
    onSurfaceVariant = FaLightTextSecondary,
    outline = FaLightDivider
)

@Composable
fun FA97ChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
