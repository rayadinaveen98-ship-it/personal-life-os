package com.navin.personallifeos.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Moss,
    secondary = MutedGold,
    tertiary = SoftLavender,
    background = WarmIvory,
    surface = CardCream,
    surfaceVariant = ColorMist,
    onPrimary = CardCream,
    onSecondary = Ink,
    onTertiary = Ink,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Ink,
)

private val DarkColors = darkColorScheme(
    primary = DarkMoss,
    secondary = MutedGold,
    tertiary = SoftLavender,
    background = DeepInk,
    surface = DarkCard,
    surfaceVariant = DarkCardSoft,
    onPrimary = CardCream,
    onSecondary = DeepInk,
    onTertiary = DeepInk,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkTextMuted,
)

@Composable
fun PersonalLifeOsTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content,
    )
}
