package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography

/**
 * Displays the current vehicle speed with a half-circle gauge.
 * Includes cruise control indicator and speed.
 *
 * @param speed Current speed in mph
 * @param speedUnit Unit of speed measurement (mph, km/h)
 * @param maxSpeed Maximum speed value for gauge scaling
 * @param cruiseControlSpeed Optional cruise control speed (null if inactive)
 */
@Composable
fun ClusterSpeedDisplay(
    modifier: Modifier = Modifier,
    speed: Int,
    speedUnit: String,
    cruiseControl: Boolean,
    maxSpeed: Float = 180f,
    cruiseControlSpeed: Int? = null
) {
    val typography = LocalClusterTypography.current

    val animatedSpeed by animateFloatAsState(
        targetValue = speed.toFloat(),
        label = "speed"
    )

    val rotationAngle by remember(speed, maxSpeed) {
        derivedStateOf { ((speed.toFloat() / maxSpeed) * 213f).coerceIn(0f, 213f) }
    }

    val speedText = remember(animatedSpeed) {
        animatedSpeed.toInt().toString()
    }

    val gaugePainter = painterResource(id = R.drawable.ic_speedometer_gauge)
    val ellipsePainter = painterResource(id = R.drawable.ic_speedometer_ellipse)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.size(684.dp, 684.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = gaugePainter,
                contentDescription = "Gauge Base",
                modifier = Modifier.fillMaxSize()
            )

            Image(
                painter = ellipsePainter,
                contentDescription = "Gauge Gradient",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = rotationAngle
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    }
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = speedText,
                    style = typography.mainSpeed
                )
                Text(
                    text = speedUnit,
                    style = typography.subSpeedRpm
                )
            }

            // Cruise Control Display
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 130.dp),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = if(cruiseControl) painterResource(id = R.drawable.ic_cruise_control_active) else painterResource(id = R.drawable.ic_cruise_control_default),
                    contentDescription = "Cruise Control Icon",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (cruiseControl) cruiseControlSpeed.toString() else "----",
                    style = typography.rangeValueText, color = if(cruiseControl) Color.Green else Color.Gray
                )

            }
        }
    }
}

@Preview()
@Composable
fun ClusterSpeedDisplayPreview() {
    ClusterSpeedDisplay(
        modifier = Modifier
            .fillMaxSize()
            .size(684.dp),
        speed = 60,
        speedUnit = "km/h",
        maxSpeed = 180f,
        cruiseControl = true,
        cruiseControlSpeed = 100
    )
}
