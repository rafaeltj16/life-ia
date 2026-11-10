package com.example.core.design

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Dark Color Scheme - Tech Noir / Deep Slate & Electric Violet
val LifeOSDarkColorScheme = darkColorScheme(
    primary = LifeOSPrimaryDark,
    onPrimary = LifeOSOnPrimaryDark,
    primaryContainer = LifeOSPrimaryContainerDark,
    onPrimaryContainer = LifeOSOnPrimaryContainerDark,
    secondary = LifeOSSecondaryDark,
    onSecondary = LifeOSOnSecondaryDark,
    secondaryContainer = LifeOSSecondaryContainerDark,
    onSecondaryContainer = LifeOSOnSecondaryContainerDark,
    tertiary = LifeOSTertiaryDark,
    onTertiary = LifeOSOnTertiaryDark,
    tertiaryContainer = LifeOSTertiaryContainerDark,
    onTertiaryContainer = LifeOSOnTertiaryContainerDark,
    error = LifeOSError,
    onError = LifeOSOnError,
    background = LifeOSBackgroundDark,
    onBackground = LifeOSOnBackgroundDark,
    surface = LifeOSSurfaceDark,
    onSurface = LifeOSOnSurfaceDark,
    surfaceVariant = LifeOSSurfaceVariantDark,
    onSurfaceVariant = LifeOSOnSurfaceVariantDark,
    surfaceContainer = LifeOSSurfaceContainerDark,
    surfaceContainerLow = LifeOSSurfaceContainerDark,
    surfaceContainerHigh = LifeOSSurfaceContainerHighDark,
    outline = LifeOSOutlineDark,
    outlineVariant = LifeOSOutlineVariantDark
)

// Light Color Scheme - Clean Tech Modern / Indigo & Cyan Accents
val LifeOSLightColorScheme = lightColorScheme(
    primary = LifeOSPrimaryLight,
    onPrimary = LifeOSOnPrimaryLight,
    primaryContainer = LifeOSPrimaryContainerLight,
    onPrimaryContainer = LifeOSOnPrimaryContainerLight,
    secondary = LifeOSSecondaryLight,
    onSecondary = LifeOSOnSecondaryLight,
    secondaryContainer = LifeOSSecondaryContainerLight,
    onSecondaryContainer = LifeOSOnSecondaryContainerLight,
    tertiary = LifeOSTertiaryLight,
    onTertiary = LifeOSOnTertiaryLight,
    tertiaryContainer = LifeOSTertiaryContainerLight,
    onTertiaryContainer = LifeOSOnTertiaryContainerLight,
    error = LifeOSError,
    onError = LifeOSOnError,
    background = LifeOSBackgroundLight,
    onBackground = LifeOSOnBackgroundLight,
    surface = LifeOSSurfaceLight,
    onSurface = LifeOSOnSurfaceLight,
    surfaceVariant = LifeOSSurfaceVariantLight,
    onSurfaceVariant = LifeOSOnSurfaceVariantLight,
    surfaceContainer = LifeOSSurfaceContainerLight,
    outline = LifeOSOutlineLight,
    outlineVariant = LifeOSOutlineVariantLight
)

/**
 * LifeOS AI Custom Material 3 Theme.
 *
 * @param darkTheme Whether dark mode is enabled.
 * @param dynamicColor Whether to use Android 12+ dynamic system coloring (defaults to false to preserve LifeOS AI signature aesthetic).
 */
@Composable
fun LifeOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LifeOSDarkColorScheme
        else -> LifeOSLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Legacy alias for seamless backwards compatibility.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LifeOSTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
