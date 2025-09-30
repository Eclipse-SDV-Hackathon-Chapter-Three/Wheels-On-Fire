package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.core.designsystem.theme.ClusterColors
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography
import com.example.digitalclusterapp.core.domain.model.ClusterState

/**
 * Displays the current transmission gear selection.
 * Optimized to prevent unnecessary style calculations.
 *
 * @param state Current cluster state containing gear information
 * @param modifier Modifier for the component
 */
@Composable
fun ClusterGearIndicator(
    modifier: Modifier = Modifier,
    state: ClusterState
) {
    val typography = LocalClusterTypography.current

    // Remember text styles to avoid recalculation
    val mainGearStyle = remember { typography.mainGear }
    val subGearStyle = remember { typography.subGear }

    // Remember highlight color to avoid recreation
    val highlightColor = remember { ClusterColors.ClusterInfoBlue }

    // Remember current gear for efficient comparisons
    val currentGear = remember(state.gear) { state.gear }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf('P', 'N', 'D', 'R').forEach { gear ->
            val active = gear == currentGear

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = gear.toString(),
                    style = if (active) mainGearStyle else subGearStyle
                )

                if (active) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.dp)
                            .background(highlightColor)
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}