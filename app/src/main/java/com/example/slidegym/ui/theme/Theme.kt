package com.example.slidegym.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Definindo as cores personalizadas
private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)
private val DarkGray = Color(0xFF121212)
private val MediumGray = Color(0xFF242424)
private val LightGray = Color(0xFF363636)
private val AccentRed = Color(0xFFE53935)
private val AccentGreen = Color(0xFF43A047)

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,
    secondary = AccentGreen,
    tertiary = White,
    background = DarkGray,
    surface = MediumGray,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = White,
    onSurface = White,
    primaryContainer = MediumGray,
    secondaryContainer = AccentGreen.copy(alpha = 0.2f),
    surfaceVariant = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = AccentRed,
    secondary = AccentGreen,
    tertiary = DarkGray,
    background = White,
    surface = Color(0xFFF5F5F5),
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
    primaryContainer = Color(0xFFFFECEB),
    secondaryContainer = AccentGreen.copy(alpha = 0.1f),
    surfaceVariant = Color(0xFFE1E1E1)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

