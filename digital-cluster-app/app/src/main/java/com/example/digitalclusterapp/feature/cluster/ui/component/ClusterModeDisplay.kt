package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography

/**
 * Displays the current driving mode information.
 * Optimized with remember to prevent unnecessary text style calculations.
 *
 * @param modeTop Top mode text (previous mode)
 * @param modeMid Middle mode text (current mode)
 * @param modeBottom Bottom mode text (next mode)
 * @param modifier Modifier for the component
 */
@Composable
fun ClusterModeDisplay(
    modifier: Modifier = Modifier,
    modeTop: String,
    modeMid: String,
    modeBottom: String
) {
    val typography = LocalClusterTypography.current

    // Remember text styles to avoid recalculation
    val mainStyle = remember { typography.mainCenter }
    val subStyle = remember { typography.subCenter }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = modeTop,
            style = subStyle
        )
        Text(
            text = modeMid,
            style = mainStyle
        )
        Text(
            text = modeBottom,
            style = subStyle
        )
    }
}