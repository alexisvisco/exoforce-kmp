package com.exoforce.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import exoforce.composeapp.generated.resources.HelveticaNeueRoman
import exoforce.composeapp.generated.resources.NeueMachina_Regular
import exoforce.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun appFontFamily() = FontFamily(
    Font(Res.font.HelveticaNeueRoman),
)

@Composable
fun displayFontFamily() = FontFamily(
    Font(Res.font.NeueMachina_Regular),
)

@Composable
fun appTypography(): Typography {
    val appFontFamily = appFontFamily()
    val displayFontFamily = displayFontFamily()
    val defaultTypography = Typography()
    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = displayFontFamily),

        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = appFontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = appFontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = appFontFamily),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = appFontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = appFontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = appFontFamily),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = appFontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = appFontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = appFontFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = appFontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = appFontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = appFontFamily)
    )
}
