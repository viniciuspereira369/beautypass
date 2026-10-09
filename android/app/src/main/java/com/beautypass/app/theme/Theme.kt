package com.beautypass.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// =====================================================================
// DESIGN SYSTEM OFICIAL BEAUTYPASS — TEMA MATERIAL 3 (THE TRIPLE FUSION)
// =====================================================================

private val LightColorScheme = lightColorScheme(
    primary = SereneTeal,
    onPrimary = SurfaceWhite,
    primaryContainer = SereneTealContainer,
    onPrimaryContainer = SereneTealOnContainer,
    secondary = MintPrimary,
    onSecondary = SurfaceWhite,
    secondaryContainer = MintContainer,
    onSecondaryContainer = MintOnContainer,
    tertiary = CoralPromo,
    onTertiary = SurfaceWhite,
    tertiaryContainer = CoralPromoBg,
    onTertiaryContainer = CoralPromoText,
    background = CanvasBase,
    onBackground = NeutralText,
    surface = SurfaceWhite,
    onSurface = NeutralText,
    surfaceVariant = MintSurface,
    onSurfaceVariant = NeutralMuted,
    surfaceTint = MintSurface,
    outline = OutlineBorder,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onError = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = MintLight,
    onPrimary = Slate900,
    primaryContainer = SereneTealDark,
    onPrimaryContainer = MintLight,
    secondary = MintPrimary,
    onSecondary = Slate900,
    secondaryContainer = SereneTeal,
    onSecondaryContainer = MintSurface,
    tertiary = CoralPromo,
    onTertiary = Slate900,
    tertiaryContainer = Slate800,
    onTertiaryContainer = CoralPromo,
    background = Slate900,
    surface = Slate800,
    onBackground = Slate50,
    onSurface = Slate50,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = Slate600,
    outlineVariant = Slate800,
    error = ErrorRed,
    onError = Slate900
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
        shapes = Shapes,
        content = content
    )
}
