package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.components.GaugeRenderer
import com.example.digitalclusterapp.core.designsystem.theme.ClusterColors
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography

// Battery gauge constants
private const val BATTERY_GAUGE_START_ANGLE = 270f // Top
private const val BATTERY_GAUGE_SWEEP_ANGLE = 180f // Half circle
private const val BATTERY_GAUGE_ROTATION = 30f // Rotation angle
private const val BATTERY_STROKE_WIDTH = 35f

// Colors
private val BATTERY_BACKGROUND_COLOR = ClusterColors.gaugeBackgroundColor
private val BATTERY_GAUGE_START_COLOR = ClusterColors.batteryGaugeStartColor
private val BATTERY_GAUGE_END_COLOR = ClusterColors.batteryGaugeEndColor

/**
 * Displays the battery charge level with a half-circle gauge.
 *
 * @param chargeLevel Current battery charge level (0.0 to 1.0)
 * @param range Current range in the specified unit
 * @param rangeUnit Unit for the range display (e.g., "km", "mi")
 * @param modifier Modifier for the component
 */
@Composable
fun ClusterBatteryDisplay(
    modifier: Modifier = Modifier,
    chargeLevel: Float,
    range: Float,
    rangeUnit: String
) {
    val typography = LocalClusterTypography.current

    Box(
        modifier = modifier
            .size(width = 540.dp, height = 540.dp),
        contentAlignment = Alignment.Center
    ) {
        val animatedChargeLevel by animateFloatAsState(
            targetValue = chargeLevel.coerceIn(0f, 1f),
            label = "battery-charge"
        )

        val progress = animatedChargeLevel.coerceIn(0f, 1f)

        // Column for values and icon
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Spacer(
                    modifier = Modifier.width(90.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_battery_level_full),
                    contentDescription = "Charge",
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 20.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(100.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // Center: Charge Value
                Text(
                    text = "${(animatedChargeLevel * 100).toInt()}",
                    style = typography.batteryChargeText,
                    modifier = Modifier.alignByBaseline()
                )
                // Center: Percentage
                Text(
                    text ="%",
                    style = typography.batteryChargePercentageText,
                    modifier = Modifier.alignByBaseline()
                )
            }

            // Below percentage: Range with unit
            Text(
                text = "${range.toInt()} $rangeUnit",
                style = typography.rangeValueText,
                modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)
            )
            
            // Bottom: Charge icon
            Image(
                painter = painterResource(id = R.drawable.ic_charge),
                contentDescription = "Charge",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 25.dp)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }

        // Box for gauge (overlapping the Column)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BatteryGauge(
                modifier = Modifier.fillMaxSize(),
                chargeLevel = progress
            )
        }

    }
}

/**
 * Draws the battery gauge using GaugeRenderer.
 *
 * @param modifier Modifier for the component
 * @param chargeLevel Current battery charge level (0.0 to 1.0)
 */
@Composable
private fun BatteryGauge(
    modifier: Modifier = Modifier,
    chargeLevel: Float
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val strokeWidth = with(density) { BATTERY_STROKE_WIDTH.dp.toPx() }
        
        // Calculate gauge size so that the stroke is tangent to the box sides
        // The gauge radius should be (box_size - stroke_width) / 2
        val gaugeSize = minOf(size.width, size.height) - strokeWidth
        
        // Calculate arc center (centered in the container)
        val arcCenter = Offset(
            x = size.width / 2f,
            y = size.height / 2f
        )

        // Apply rotation around the center
        rotate(
            degrees = BATTERY_GAUGE_ROTATION,
            pivot = arcCenter
        ) {
            // Create gauge renderer
            val batteryGaugeRenderer = GaugeRenderer(
                drawScope = this,
                arcCenter = arcCenter,
                arcSize = gaugeSize,
                strokeWidth = strokeWidth
            )

            // Calculate progress sweep angle
            val progressSweep = BATTERY_GAUGE_SWEEP_ANGLE * chargeLevel

            // Draw background arc
            batteryGaugeRenderer.drawBackgroundArc(
                startAngle = BATTERY_GAUGE_START_ANGLE,
                sweepAngle = BATTERY_GAUGE_SWEEP_ANGLE,
                backgroundColor = BATTERY_BACKGROUND_COLOR
            )

            // Draw progress arc with glow
            batteryGaugeRenderer.drawProgressArcGradient(
                startAngle = -BATTERY_GAUGE_START_ANGLE,
                sweepAngle = -progressSweep,
                startColor = BATTERY_GAUGE_END_COLOR,
                endColor = BATTERY_GAUGE_START_COLOR,
                gradientStartPosition = 0f
            )
        }
    }
}

@Preview(widthDp = 600, heightDp = 600)
@Composable
fun PreviewBattery() {
    ClusterBatteryDisplay(
        modifier = Modifier
            .fillMaxSize(),
        chargeLevel = 0.23f,
        range = 450f,
        rangeUnit = "km",
    )
}