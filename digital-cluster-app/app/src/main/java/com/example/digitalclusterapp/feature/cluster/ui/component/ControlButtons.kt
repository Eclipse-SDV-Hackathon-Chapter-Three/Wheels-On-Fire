package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.theme.ClusterColors
import com.example.digitalclusterapp.core.domain.model.ClusterState
import com.example.digitalclusterapp.core.domain.action.ClusterAction

/**
 * Control buttons for interacting with the cluster display.
 * Optimized with remember for colors.
 *
 * @param modifier Modifier for the component
 * @param onAction Callback for button actions
 * @param state Current cluster state
 */
@Composable
fun ControlButtons(
    modifier: Modifier = Modifier,
    onAction: (ClusterAction) -> Unit,
    state: ClusterState
) {
    // Remember colors to avoid recreation
    val backgroundColor = remember { ClusterColors.BlueBlack }

    // Get painters directly in the composable context
    val plusIconPainter = painterResource(
        id = if (state.cruiseControl) R.drawable.ic_plus_active
        else R.drawable.ic_plus
    )

    val minusIconPainter = painterResource(
        id = if (state.cruiseControl) R.drawable.ic_minus_active
        else R.drawable.ic_minus
    )

    val cruiseControlPainter = painterResource(
        id = if (state.cruiseControl) R.drawable.ic_cruise_control_active
        else R.drawable.ic_cruise_control_default
    )

    val locationPainter = painterResource(
        id = if (state.location) R.drawable.ic_location_active
        else R.drawable.ic_location_default
    )

    // Fixed painters that don't change with state
    val frontSensorPainter = painterResource(id = R.drawable.ic_front_parking_sensor)
    val rearSensorPainter = painterResource(id = R.drawable.ic_rear_parking_sensor)
    val rightSensorPainter = painterResource(id = R.drawable.ic_right_parking_sensor)
    val leftSensorPainter = painterResource(id = R.drawable.ic_left_parking_sensor)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speed controls
        IconButton(onClick = { onAction(ClusterAction.Speed.Increase) }) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = plusIconPainter,
                contentDescription = "Increase Speed",
            )
        }

        // Decrease speed
        IconButton(onClick = { onAction(ClusterAction.Speed.Decrease) }) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = minusIconPainter,
                contentDescription = "Decrease Speed",
            )
        }

        // Activate Cruise Control
        IconButton(onClick = { onAction(ClusterAction.ToggleCruiseControl) }) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = cruiseControlPainter,
                contentDescription = "Toggle Cruise Control",
            )
        }

        // Front Parking Sensor
        IconButton(onClick = { onAction(ClusterAction.ParkingSensor.Front) }) {
            Image(
                modifier = Modifier.size(36.dp),
                painter = frontSensorPainter,
                contentDescription = "Front Parking Sensor",
            )
        }

        // Rear Parking Sensor
        IconButton(onClick = { onAction(ClusterAction.ParkingSensor.Rear) }) {
            Image(
                modifier = Modifier.size(36.dp),
                painter = rearSensorPainter,
                contentDescription = "Rear Parking Sensor",
            )
        }

        // Location
        IconButton(onClick = { onAction(ClusterAction.ToggleLocation) }) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = locationPainter,
                contentDescription = "Location",
            )
        }

        // Right Parking Sensor
        IconButton(onClick = { onAction(ClusterAction.ParkingSensor.Right) }) {
            Image(
                modifier = Modifier.size(46.dp),
                painter = rightSensorPainter,
                contentDescription = "Right Parking Sensor",
            )
        }

        // Left Parking Sensor
        IconButton(onClick = { onAction(ClusterAction.ParkingSensor.Left) }) {
            Image(
                modifier = Modifier.size(46.dp),
                painter = leftSensorPainter,
                contentDescription = "Left Parking Sensor",
            )
        }
    }
}