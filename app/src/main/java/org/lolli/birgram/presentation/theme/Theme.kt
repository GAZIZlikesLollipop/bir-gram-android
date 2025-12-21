package org.lolli.birgram.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.lolli.birgram.R

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun birGramFontFamily() = FontFamily(
    Font(
        R.font.inter_normal,
        FontWeight.Normal,
        FontStyle.Normal,
    ),
    Font(
        R.font.inter_italic,
        FontWeight.Normal,
        FontStyle.Italic
    )
)

@Composable
fun BirGramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val baseline = Typography()
    val typography = Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = birGramFontFamily()),
        displayMedium = baseline.displayMedium.copy(fontFamily = birGramFontFamily()),
        displaySmall = baseline.displaySmall.copy(fontFamily = birGramFontFamily()),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = birGramFontFamily()),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = birGramFontFamily()),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = birGramFontFamily()),
        titleLarge = baseline.titleLarge.copy(fontFamily = birGramFontFamily()),
        titleMedium = baseline.titleMedium.copy(fontFamily = birGramFontFamily()),
        titleSmall = baseline.titleSmall.copy(fontFamily = birGramFontFamily()),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = birGramFontFamily()),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = birGramFontFamily()),
        bodySmall = baseline.bodySmall.copy(fontFamily = birGramFontFamily()),
        labelLarge = baseline.labelLarge.copy(fontFamily = birGramFontFamily()),
        labelMedium = baseline.labelMedium.copy(fontFamily = birGramFontFamily()),
        labelSmall = baseline.labelSmall.copy(fontFamily = birGramFontFamily()),
    )
    val colorScheme = when {
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

