// Theme.kt
package com.example.slidegym.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Cores inspiradas no Discord
private val DiscordBackground = Color(0xFF36393F)
private val DiscordSurface = Color(0xFF2F3136)
private val DiscordPrimary = Color(0xFF5865F2)
private val DiscordSecondary = Color(0xFF3BA55C)
private val DiscordError = Color(0xFFED4245)
private val DiscordText = Color(0xFFDCDDDE)
private val DiscordTextSecondary = Color(0xFF96989D)
private val DiscordDivider = Color(0xFF40444B)
private val DiscordHover = Color(0xFF32353B)
private val DiscordCard = Color(0xFF2F3136)

private val DarkColorScheme = darkColorScheme(
    primary = DiscordPrimary,
    secondary = DiscordSecondary,
    background = DiscordBackground,
    surface = DiscordSurface,
    error = DiscordError,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DiscordText,
    onSurface = DiscordText,
    primaryContainer = DiscordHover,
    secondaryContainer = DiscordSecondary.copy(alpha = 0.2f),
    surfaceVariant = DiscordCard
)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = true, // Forçando tema escuro como padrão
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Sempre usando o tema escuro

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}