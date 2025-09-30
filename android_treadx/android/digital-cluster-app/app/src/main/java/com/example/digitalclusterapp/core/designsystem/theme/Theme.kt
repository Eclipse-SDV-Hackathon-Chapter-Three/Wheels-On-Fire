package com.example.digitalclusterapp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Local provider for cluster typography.
 */
val LocalClusterTypography = staticCompositionLocalOf { ClusterTypography }

/**
 * Local provider for cluster colors.
 */
val LocalClusterColors = staticCompositionLocalOf { ClusterColors }

private val DarkColorScheme = darkColorScheme(
    primary = ClusterColors.DarkBlue,
    secondary = ClusterColors.LightBlue,
    background = ClusterColors.DarkBackground,
    surface = ClusterColors.DarkerBackground,
    onPrimary = ClusterColors.TextWhite,
    onSecondary = ClusterColors.TextWhite,
    onBackground = ClusterColors.TextWhite,
    onSurface = ClusterColors.TextWhite
)

private val LightColorScheme = lightColorScheme(
    // Light theme colors for tunnels and other bright environments
    primary = ClusterColors.DarkBlue,
    secondary = ClusterColors.LightBlue,
    background = ClusterColors.TextWhite,
    surface = ClusterColors.TextWhite.copy(alpha = 0.9f),
    onPrimary = ClusterColors.DarkerBackground,
    onSecondary = ClusterColors.DarkerBackground,
    onBackground = ClusterColors.DarkerBackground,
    onSurface = ClusterColors.DarkerBackground
)

/**
 * Theme for the Digital Cluster application.
 *
 * @param darkTheme Whether to use dark theme
 * @param content Content to be themed
 */
@Composable
fun DigitalClusterAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalClusterTypography provides ClusterTypography,
        LocalClusterColors provides ClusterColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}