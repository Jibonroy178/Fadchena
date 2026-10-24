package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyberTeal,
    onPrimary = BrandNavyDark,
    primaryContainer = BrandNavyElevatedDark,
    onPrimaryContainer = CyberTealLight,
    secondary = CyberCyan,
    onSecondary = BrandNavyDark,
    tertiary = RiskSuspiciousOrange,
    background = BrandNavyDark,
    onBackground = TextPrimaryDark,
    surface = BrandNavyCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = BrandNavyElevatedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = RiskHighRed,
    onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = BrandNavyLight,
    onPrimary = BrandSurfaceLight,
    primaryContainer = BrandSurfaceVariantLight,
    onPrimaryContainer = BrandNavyLight,
    secondary = CyberTealDark,
    onSecondary = BrandSurfaceLight,
    tertiary = RiskSuspiciousOrange,
    background = BrandBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = BrandSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BrandSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = RiskHighRed,
    onError = BrandSurfaceLight
)

@Composable
fun FadChenaTheme(
    darkTheme: Boolean = true, // Default to modern cybersecurity dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
