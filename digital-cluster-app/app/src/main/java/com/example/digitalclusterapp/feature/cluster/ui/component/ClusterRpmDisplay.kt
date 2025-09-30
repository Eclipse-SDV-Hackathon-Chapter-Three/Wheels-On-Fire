package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.components.GaugeRenderer
import com.example.digitalclusterapp.core.designsystem.theme.ClusterColors
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography

// Global constants
private const val BOX_SIZE_DP = 468f
private const val ARC_SIZE_DP = 468f
private const val BOTTOM_ARC_START_ANGLE = 45f
private const val ARC_OFFSET_Y = 10f

// Stroke width controls
private const val BACKGROUND_STROKE_WIDTH = 8f
private const val PROGRESS_STROKE_WIDTH = 8f

// Colors
private val BACKGROUND_COLOR = ClusterColors.gaugeBackgroundColor
private val RPM_ARC_COLOR = ClusterColors.rpmGaugeColor // Bright orange for RPM arc
private val TEMPERATURE_ARC_COLOR = ClusterColors.engineTempGaugeColor // Red for temperature arc
private val RPM_GLOW_COLOR = RPM_ARC_COLOR.copy(alpha = 0.3f)
private val TEMPERATURE_GLOW_COLOR = TEMPERATURE_ARC_COLOR.copy(alpha = 0.3f)

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

/**
 * Displays the current engine RPM with a half-circle gauge.
 * Optimized for performance with remember and memoization.
 *
 * @param rpm Current RPM value
 * @param modifier Modifier for the component
 * @param engineTemp Temperature value
 * @param tempUnit Temperature unit (Celsius or Fahrenheit)
 */
@Composable
fun ClusterRpmDisplay(
    modifier: Modifier = Modifier,
    rpm: Float,
    engineTemp: Float,
    tempUnit: TemperatureUnit = TemperatureUnit.CELSIUS
) {
    val typography = LocalClusterTypography.current

    // Format text values only when inputs change
    val rpmText = remember(rpm) {
        String.format("%.1f", rpm)
    }

    val tempText = remember(engineTemp, tempUnit) {
        val unit = if (tempUnit == TemperatureUnit.CELSIUS) "C°" else "F°"
        "$engineTemp $unit"
    }

    // Get painter directly in the composable context
    val gaugePainter = painterResource(id = R.drawable.ic_rpm_gauge)

    Box(
        modifier = modifier.size(width = BOX_SIZE_DP.dp, height = BOX_SIZE_DP.dp),
        contentAlignment = Alignment.Center
    ) {
        // Main image
        Image(
            painter = gaugePainter,
            contentDescription = "RPM base",
            modifier = Modifier.size(BOX_SIZE_DP.dp)
        )

        // Draw all arcs in a single Canvas
        ClusterArcs(
            modifier = Modifier.fillMaxSize(),
            rpm = rpm,
            temp = engineTemp,
            tempUnit = tempUnit
        )

        // RPM Value
        Text(
            text = rpmText,
            style = typography.mainRPM,
            modifier = Modifier.align(Alignment.Center)
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            style = typography.temperatureText,
            text = tempText
        )
    }
}

// RPM constants
private const val MAX_RPM = 6f
private const val TOP_ARC_START_ANGLE = 162f
private const val TOP_ARC_SWEEP_ANGLE = 216f

// Temperature constants
private const val CELSIUS_50_PERCENT = 90f
private const val FAHRENHEIT_50_PERCENT = 195f
private const val TEMP_MAX_VALUE = 200f
private const val BOTTOM_ARC_SWEEP_ANGLE = 90f
private const val TEMP_ARC_START_ANGLE = 135f

// Helper functions
private fun calculateRpmAngle(rpm: Float): Float =
    ((rpm * TOP_ARC_SWEEP_ANGLE) / MAX_RPM).coerceIn(0f, TOP_ARC_SWEEP_ANGLE)

private fun convertTemperatureToProgress(temp: Float, unit: TemperatureUnit): Float {
    val progress = when (unit) {
        TemperatureUnit.CELSIUS -> {
            val normalizedTemp = (temp - CELSIUS_50_PERCENT) / (TEMP_MAX_VALUE - CELSIUS_50_PERCENT)
            0.5f + (normalizedTemp * 0.5f)
        }

        TemperatureUnit.FAHRENHEIT -> {
            val fahrenheitMax = (TEMP_MAX_VALUE * 9f / 5f) + 32f
            val normalizedTemp =
                (temp - FAHRENHEIT_50_PERCENT) / (fahrenheitMax - FAHRENHEIT_50_PERCENT)
            0.5f + (normalizedTemp * 0.5f)
        }
    }
    return progress.coerceIn(0f, 1f)
}

/**
 * Data class to hold arc properties for efficient drawing
 */
private data class ArcProperties(
    val topArcCenter: Offset,
    val bottomArcCenter: Offset,
    val arcSize: Float,
    val backgroundStrokeWidth: Float,
    val progressStrokeWidth: Float
)

/**
 * Draws all cluster arcs in a single Canvas for better performance
 * Optimized with remember to avoid recalculations
 */
@Composable
private fun ClusterArcs(
    modifier: Modifier = Modifier,
    rpm: Float,
    temp: Float,
    tempUnit: TemperatureUnit
) {
    val density = LocalDensity.current

    // Calculate these values only when inputs change
    val rpmProgressSweep = remember(rpm) {
        calculateRpmAngle(rpm)
    }

    val tempProgress = remember(temp, tempUnit) {
        convertTemperatureToProgress(temp, tempUnit)
    }

    val tempProgressSweep = remember(tempProgress) {
        BOTTOM_ARC_SWEEP_ANGLE * tempProgress
    }

    Canvas(modifier = modifier) {
        // Calculate properties once per drawing session
        val arcProperties = calculateArcProperties(density, size.width, size.height)

        // Create renderers for RPM and Engine temperature gauges
        val rpmGaugeRenderer = GaugeRenderer(
            drawScope = this,
            arcCenter = arcProperties.topArcCenter,
            arcSize = arcProperties.arcSize,
            strokeWidth = arcProperties.progressStrokeWidth
        )

        val engineTemperatureGaugeRenderer = GaugeRenderer(
            drawScope = this,
            arcCenter = arcProperties.bottomArcCenter,
            arcSize = arcProperties.arcSize,
            strokeWidth = arcProperties.progressStrokeWidth
        )

        // Draw background arcs
        rpmGaugeRenderer.drawBackgroundArc(
            startAngle = TOP_ARC_START_ANGLE,
            sweepAngle = TOP_ARC_SWEEP_ANGLE,
            backgroundColor = BACKGROUND_COLOR
        )

        engineTemperatureGaugeRenderer.drawBackgroundArc(
            startAngle = BOTTOM_ARC_START_ANGLE,
            sweepAngle = BOTTOM_ARC_SWEEP_ANGLE,
            backgroundColor = BACKGROUND_COLOR
        )

        // Draw RPM progress arc with glow
        rpmGaugeRenderer.drawProgressArc(
            startAngle = TOP_ARC_START_ANGLE,
            sweepAngle = rpmProgressSweep,
            progressColor = RPM_ARC_COLOR,
            glowColor = RPM_GLOW_COLOR,
            showGlow = true
        )

        // Draw Engine temperature progress arc with glow
        engineTemperatureGaugeRenderer.drawProgressArc(
            startAngle = TEMP_ARC_START_ANGLE,
            sweepAngle = -tempProgressSweep,
            progressColor = TEMPERATURE_ARC_COLOR,
            glowColor = TEMPERATURE_GLOW_COLOR,
            showGlow = true
        )
    }
}

// Helper functions
private fun calculateArcProperties(density: Density, width: Float, height: Float): ArcProperties {
    val backgroundStrokeWidth = with(density) { BACKGROUND_STROKE_WIDTH.dp.toPx() }
    val progressStrokeWidth = with(density) { PROGRESS_STROKE_WIDTH.dp.toPx() }
    val boxCenter = Offset(x = width / 2f, y = height / 2f)
    val arcSize = with(density) { ARC_SIZE_DP.dp.toPx() }

    val topArcCenter = Offset(x = boxCenter.x, y = (arcSize / 2f) + ARC_OFFSET_Y)
    val bottomArcCenter = Offset(x = boxCenter.x, y = height - arcSize / 2f)

    return ArcProperties(
        topArcCenter,
        bottomArcCenter,
        arcSize,
        backgroundStrokeWidth,
        progressStrokeWidth
    )
}
