package com.smartreturn.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// SmartReturn AI always uses its custom dark theme for the premium AI aesthetic
private val SmartReturnDarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = CyanOnPrimary,
    primaryContainer = CyanPrimaryVariant,
    onPrimaryContainer = TextPrimary,
    secondary = VioletSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = Color(0xFF2D1B69),
    onSecondaryContainer = Color(0xFFD0BCFF),
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = BackgroundSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = TextSecondary,
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
    error = ConfidenceLow,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF4A0000),
    onErrorContainer = Color(0xFFFFB4AB),
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDeep,
    inversePrimary = CyanPrimaryVariant,
    surfaceTint = CyanPrimary,
    scrim = Color(0xFF000000)
)

@Composable
fun SmartReturnTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SmartReturnDarkColorScheme,
        typography = SmartReturnTypography,
        content = content
    )
}
