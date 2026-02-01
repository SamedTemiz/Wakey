package com.wakey.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light color scheme for Wakey app
 * Based on coral/pink design mockups
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryCoral,
    onPrimary = Color.White,
    primaryContainer = PrimaryCoralLight,
    onPrimaryContainer = TextPrimary,
    secondary = AccentOrange,
    onSecondary = Color.White,
    secondaryContainer = AccentOrange.copy(alpha = 0.2f),
    onSecondaryContainer = TextPrimary,
    tertiary = AccentPeach,
    onTertiary = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    error = ErrorColor,
    onError = Color.White
)

/**
 * Main theme composable for Wakey app.
 * Enforces Light Theme only.
 */
@Composable
fun WakeyTheme(
    darkTheme: Boolean = false, // Parameter kept for compatibility but ignored
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    
    // Make status bar transparent with dark icons (Light Mode behavior)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
