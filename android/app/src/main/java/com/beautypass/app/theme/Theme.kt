package com.beautypass.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SereneTeal,
    onPrimary = SurfaceWhite,
    primaryContainer = MintSurface,
    onPrimaryContainer = SereneTealDark,
    secondary = MintPrimary,
    onSecondary = SurfaceWhite,
    tertiary = CoralPromo,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = Slate900,
    onSurface = Slate900,
    outline = OutlineBorder,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = MintLight,
    onPrimary = Slate900,
    primaryContainer = SereneTealDark,
    onPrimaryContainer = MintLight,
    secondary = MintPrimary,
    background = Slate900,
    surface = Slate800,
    onBackground = Slate50,
    onSurface = Slate50,
    outline = Slate600,
    error = ErrorRed
)

@Composable
fun BeautyPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
