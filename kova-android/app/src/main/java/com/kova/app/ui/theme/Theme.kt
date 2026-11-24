package com.kova.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = KovaIndigo,
    onPrimary = Color.White,
    primaryContainer = KovaIndigoContainer,
    onPrimaryContainer = Color(0xFF1B1063),
    secondary = Color(0xFF5B51C9),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1DFFF),
    onSecondaryContainer = Color(0xFF17076B),
    tertiary = Color(0xFF3E6BC0),
    onTertiary = Color.White,
    surface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFE7E0F3),
    onSurfaceVariant = Color(0xFF49454F)
)

private val DarkColors = darkColorScheme(
    primary = KovaIndigoDark,
    onPrimary = Color(0xFF230F83),
    primaryContainer = Color(0xFF4B3AB5),
    onPrimaryContainer = Color(0xFFE4DEFF),
    secondary = Color(0xFFC4C1FF),
    onSecondary = Color(0xFF2A1E7F),
    secondaryContainer = Color(0xFF413A97),
    onSecondaryContainer = Color(0xFFE1DFFF),
    tertiary = Color(0xFFADC6FF),
    onTertiary = Color(0xFF00296F),
    surface = Color(0xFF141318),
    surfaceVariant = Color(0xFF2B2930),
    onSurfaceVariant = Color(0xFFCAC4D4)
)

@Composable
fun KovaTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = KovaTypography,
        content = content
    )
}
