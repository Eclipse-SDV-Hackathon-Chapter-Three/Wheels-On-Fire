package com.example.digitalclusterapp.feature.cluster.ui.component

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.core.domain.model.CentralScreenState

/**
 * Displays the central screen content based on the current state.
 * Optimized to prevent unnecessary recompositions.
 *
 * @param modifier Modifier for the component
 * @param current The current central screen state to display
 */
@Composable
fun ClusterMiddleDisplay(
    modifier: Modifier = Modifier,
    current: CentralScreenState = CentralScreenState.MODES,
) {
    // Use when statement directly without unnecessary variables
    when (current) {
        CentralScreenState.MODES -> ClusterModeDisplay(
            modifier = modifier,
            modeTop = "Race",
            modeMid = "Sport+",
            modeBottom = "City"
        )
        CentralScreenState.MAP -> ClusterMapDisplay(modifier)
        CentralScreenState.SENSORS_FORWARD -> ClusterSensorCollisionDisplay(modifier.padding(start = 80.dp))
        CentralScreenState.SENSORS_BLIND -> ClusterSensorBlindDisplay(modifier.padding(start = 80.dp))
    }
}