package com.example.digitalclusterapp.core.designsystem.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * A reusable class for drawing gauge arcs with optional glow effects.
 * This class encapsulates common gauge drawing functionality that can be used
 * across different cluster components.
 *
 * @param drawScope The DrawScope to draw on
 * @param arcCenter The center point of the arc
 * @param arcSize The diameter of the arc
 * @param strokeWidth The width of the arc stroke
 */
class GaugeRenderer(
    private val drawScope: DrawScope,
    private val arcCenter: Offset,
    private val arcSize: Float,
    private val strokeWidth: Float
) {
    
    /**
     * Draws the background track of the gauge.
     *
     * @param startAngle The starting angle in degrees (0° = 3 o'clock, 90° = 6 o'clock)
     * @param sweepAngle The angle to sweep in degrees (positive = clockwise)
     * @param backgroundColor The color of the background track
     */
    fun drawBackgroundArc(
        startAngle: Float,
        sweepAngle: Float,
        backgroundColor: Color
    ) {
        drawScope.drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            useCenter = false,
            topLeft = Offset(
                x = arcCenter.x - arcSize / 2f,
                y = arcCenter.y - arcSize / 2f
            ),
            size = Size(arcSize, arcSize)
        )
    }
    
    /**
     * Draws the progress arc with optional glow effect.
     *
     * @param startAngle The starting angle in degrees (0° = 3 o'clock, 90° = 6 o'clock)
     * @param sweepAngle The angle to sweep in degrees (positive = clockwise)
     * @param progressColor The color of the progress arc
     * @param glowColor The color for the glow effect (optional)
     * @param showGlow Whether to show the glow effect (default: true)
     * @param glowLayers Number of glow layers to draw (default: 3)
     */
    fun drawProgressArc(
        startAngle: Float,
        sweepAngle: Float,
        progressColor: Color,
        glowColor: Color? = null,
        showGlow: Boolean = true,
        glowLayers: Int = 3
    ) {
        if (showGlow && glowColor != null) {
            drawGlowEffect(
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                glowColor = glowColor,
                glowLayers = glowLayers
            )
        }
        
        // Draw main progress arc
        drawScope.drawArc(
            color = progressColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            useCenter = false,
            topLeft = Offset(
                x = arcCenter.x - arcSize / 2f,
                y = arcCenter.y - arcSize / 2f
            ),
            size = Size(arcSize, arcSize)
        )
    }
    
    /**
     * Draws the progress arc with a gradient instead of solid color.
     * Allows specifying where the gradient starts as a percentage of the total progress.
     *
     * @param startAngle The starting angle in degrees (0° = 3 o'clock, 90° = 6 o'clock)
     * @param sweepAngle The angle to sweep in degrees (positive = clockwise)
     * @param startColor The starting color of the gradient
     * @param endColor The ending color of the gradient
     * @param gradientStartPosition The position where the gradient starts (0.0 to 1.0, default 0.0)
     */
    fun drawProgressArcGradient(
        startAngle: Float,
        sweepAngle: Float,
        startColor: Color,
        endColor: Color,
        gradientStartPosition: Float = 0.0f
    ) {
        // Clamp gradient start position between 0.0 and 1.0
        val gradientStart = gradientStartPosition.coerceIn(0.0f, 1.0f)
        
        // Calculate where the gradient should start in the arc
        val gradientStartAngle = startAngle + (sweepAngle * gradientStart)
        val gradientSweepAngle = sweepAngle * (1.0f - gradientStart)
        
        // If gradient starts at the beginning, draw the full gradient
        if (gradientStart <= 0.0f) {
            val brush = createLinearGradient(startColor, endColor, sweepAngle)
            drawScope.drawArc(
                brush = brush,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                useCenter = false,
                topLeft = Offset(
                    x = arcCenter.x - arcSize / 2f,
                    y = arcCenter.y - arcSize / 2f
                ),
                size = Size(arcSize, arcSize)
            )
        } else {
            // Draw solid color for the part before gradient starts
            val solidSweep = sweepAngle * gradientStart
            if (solidSweep > 0f) {
                drawScope.drawArc(
                    color = startColor,
                    startAngle = startAngle,
                    sweepAngle = solidSweep,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    useCenter = false,
                    topLeft = Offset(
                        x = arcCenter.x - arcSize / 2f,
                        y = arcCenter.y - arcSize / 2f
                    ),
                    size = Size(arcSize, arcSize)
                )
            }
            
            // Draw gradient for the remaining part
            if (gradientSweepAngle > 0f) {
                val brush = createLinearGradient(startColor, endColor, gradientSweepAngle)
                drawScope.drawArc(
                    brush = brush,
                    startAngle = gradientStartAngle,
                    sweepAngle = gradientSweepAngle,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    useCenter = false,
                    topLeft = Offset(
                        x = arcCenter.x - arcSize / 2f,
                        y = arcCenter.y - arcSize / 2f
                    ),
                    size = Size(arcSize, arcSize)
                )
            }
        }
    }
    
    /**
     * Creates a linear gradient brush for the arc.
     * The gradient direction is adjusted based on the sweep angle direction.
     */
    private fun createLinearGradient(startColor: Color, endColor: Color, sweepAngle: Float): Brush {
        // Determine gradient direction based on sweep angle
        val isClockwise = sweepAngle > 0
        
        return if (isClockwise) {
            // Clockwise: gradient from top-left to bottom-right
            Brush.linearGradient(
                colors = listOf(startColor, endColor),
                start = Offset(
                    x = arcCenter.x - arcSize / 2f,
                    y = arcCenter.y - arcSize / 2f
                ),
                end = Offset(
                    x = arcCenter.x + arcSize / 2f,
                    y = arcCenter.y + arcSize / 2f
                )
            )
        } else {
            // Counter-clockwise: gradient from top-right to bottom-left
            Brush.linearGradient(
                colors = listOf(startColor, endColor),
                start = Offset(
                    x = arcCenter.x + arcSize / 2f,
                    y = arcCenter.y - arcSize / 2f
                ),
                end = Offset(
                    x = arcCenter.x - arcSize / 2f,
                    y = arcCenter.y + arcSize / 2f
                )
            )
        }
    }
    

    /**
     * Draws the glow effect behind the main arc.
     *
     * @param startAngle The starting angle in degrees
     * @param sweepAngle The angle to sweep in degrees
     * @param glowColor The color for the glow effect
     * @param glowLayers Number of glow layers to draw
     */
    private fun drawGlowEffect(
        startAngle: Float,
        sweepAngle: Float,
        glowColor: Color,
        glowLayers: Int
    ) {
        for (i in 1..glowLayers) {
            val glowWidth = strokeWidth + (i * 8f)
            val glowAlpha = 0.15f - (i * 0.05f)
            
            drawScope.drawArc(
                color = glowColor.copy(alpha = glowAlpha),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                style = Stroke(width = glowWidth, cap = StrokeCap.Round),
                useCenter = false,
                topLeft = Offset(
                    x = arcCenter.x - arcSize / 2f,
                    y = arcCenter.y - arcSize / 2f
                ),
                size = Size(arcSize, arcSize)
            )
        }
    }
}
