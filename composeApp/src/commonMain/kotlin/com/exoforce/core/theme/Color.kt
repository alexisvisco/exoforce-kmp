package com.exoforce.core.theme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


object BaseColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)

    val Orange400 = Color(0xFFFB923C)
    val Orange600 = Color(0xFFEA580C)
    val Orange700 = Color(0xFFC2410C)

    val Red400 = Color(0xFFF87171)
    val Red600 = Color(0xFFDC2626)
    val Red700 = Color(0xFFB91C1C)



    val Neutral100 = Color(0xFFF5F5F5)
    val Neutral300 = Color(0xFFD4D4D4)
    val Neutral700 = Color(0xFF404040)
    val Neutral800 = Color(0xFF262626)
    val Neutral900 = Color(0xFF171717)
    val Neutral950 = Color(0xFF0A0A0A)

    val Green400 = Color(0xFF05df72)
    val Green500 = Color(0xFF00c951)
    val Green600 = Color(0xFF00a63e)
}

// === Palette Light ===
val LightColors: ColorScheme = lightColorScheme(
    primary = BaseColors.Black,
    onPrimary = BaseColors.White,
    primaryContainer = BaseColors.White,
    onPrimaryContainer = BaseColors.Black,

    secondary = BaseColors.Neutral800,
    onSecondary = BaseColors.White,
    secondaryContainer = BaseColors.Neutral900,
    onSecondaryContainer = BaseColors.White,

    tertiary = BaseColors.Orange600,
    onTertiary = BaseColors.White,
    tertiaryContainer = BaseColors.Orange700,
    onTertiaryContainer = BaseColors.White,

    error = BaseColors.Red600,
    onError = BaseColors.White,
    errorContainer = BaseColors.Red700,
    onErrorContainer = BaseColors.White,

    background = BaseColors.White,
    onBackground = BaseColors.Black,

    surface = BaseColors.White,
    onSurface = BaseColors.Black,
    surfaceVariant = BaseColors.Neutral300,
    onSurfaceVariant = BaseColors.Neutral700,

    outline = BaseColors.Neutral300,
    outlineVariant = BaseColors.Neutral100,

    inverseSurface = BaseColors.Neutral900,
    inverseOnSurface = BaseColors.White,
    inversePrimary = BaseColors.Orange600,

    scrim = BaseColors.Black,
)

fun ColorScheme.lightSuccess(): Color {
    return BaseColors.Green400
}

fun ColorScheme.success(): Color {
    return BaseColors.Green500
}

fun ColorScheme.darkSuccess(): Color {
    return BaseColors.Green600
}