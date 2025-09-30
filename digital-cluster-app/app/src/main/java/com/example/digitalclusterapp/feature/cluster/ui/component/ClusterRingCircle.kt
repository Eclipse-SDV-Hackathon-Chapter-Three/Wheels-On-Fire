package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.core.designsystem.theme.ClusterColors
import kotlin.math.PI
import kotlin.math.min

/**
 * Half circle ring with a short straight tangent tail at the bottom.
 *
 * Progress runs along the combined path:
 * - SPEED (left): tail (bottom→right) then arc (bottom→left→top)
 * - RPM (right): arc (top→right→bottom) then tail (bottom→left)
 *
 * @param value Progress value from 0.0 to 1.0
 * @param side Which half of the ring to display (SPEED or RPM)
 * @param modifier Modifier for the component
 * @param thickness Thickness of the ring stroke
 * @param endExtension Length of the straight tail at the bottom
 * @param trackColor Color of the background track
 * @param progressStart Start color for the progress gradient
 * @param progressEnd End color for the progress gradient
 * @param content Content to display in the center of the ring
 */
@Composable
fun ClusterRingCircle(
    modifier: Modifier = Modifier,
    value: Float,
    side: HalfSide,
    thickness: Dp = 26.dp,
    endExtension: Dp = thickness * 1.2f,
    trackColor: Color = ClusterColors.GaugeTrack,
    progressStart: Color = ClusterColors.GaugeProgressStart,
    progressEnd: Color = ClusterColors.GaugeProgressEnd,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // Animate the progress value
    val animated by animateFloatAsState(
        targetValue = value.coerceIn(0f, 1f),
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "half-ring-progress"
    )

    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val strokePx = thickness.toPx()
            val tailPx = endExtension.toPx()
            val tailDirection = if (side == HalfSide.SPEED) 1f else -1f

            // Create a ring renderer to handle all drawing operations
            val renderer = RingRenderer(
                side = side,
                strokePx = strokePx,
                tailPx = tailPx * tailDirection,
                drawScope = this,
                trackColor = trackColor,
                progressStart = progressStart,
                progressEnd = progressEnd,
            )

            renderer.drawTrack()

            if (animated > 0f) {
                renderer.drawProgress(animated)
            }
        }

        // Overlay content
        content()
    }
}

/**
 * Helper class to encapsulate ring drawing logic.
 */
private class RingRenderer(
    val side: HalfSide,
    val strokePx: Float,
    val tailPx: Float,
    val drawScope: DrawScope,
    val trackColor: Color,
    val progressStart: Color,
    val progressEnd: Color,
) {
    // Direction settings based on side
    val startAngle = if (side == HalfSide.SPEED) 90f else 270f
    val sweepAngle = 180f
    val pathIsTailFirst = side == HalfSide.SPEED

    // Calculate dimensions once for reuse
    val dimensions = calculateDimensions()

    /**
     * Calculate dimensions for the ring.
     */
    private fun calculateDimensions(): RingDimensions {
        with(drawScope) {
            val sizeMin = min(size.width, size.height)
            val inset = strokePx / 2f + sizeMin * 0.02f
            val arcSize = Size(sizeMin - inset * 2f, sizeMin - inset * 2f)
            val topLeft = Offset(
                (size.width - arcSize.width) / 2f,
                (size.height - arcSize.height) / 2f
            )
            val center = Offset(
                x = topLeft.x + arcSize.width / 2f,
                y = topLeft.y + arcSize.height / 2f
            )
            val radius = arcSize.width / 2f
            val bottomPoint = Offset(center.x, topLeft.y + arcSize.height)
            val bottomTailEnd = bottomPoint + Offset(tailPx, 0f)

            return RingDimensions(
                arcSize = arcSize,
                topLeft = topLeft,
                center = center,
                radius = radius,
                bottomPoint = bottomPoint,
                bottomTailEnd = bottomTailEnd,
                arcLength = PI.toFloat() * radius,
                tailLength = kotlin.math.abs(tailPx)
            )
        }
    }

    /**
     * Draw the track (background).
     */
    fun drawTrack() {
        with(drawScope) {
            // Draw arc portion
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = dimensions.topLeft,
                size = dimensions.arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Draw tail portion
            drawLine(
                color = trackColor,
                start = dimensions.bottomPoint,
                end = dimensions.bottomTailEnd,
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
        }
    }

    /**
     * Draw the progress indicator.
     */
    fun drawProgress(progress: Float) {
        val totalLength = dimensions.arcLength + dimensions.tailLength
        val arcRatio = dimensions.arcLength / totalLength
        val tailRatio = dimensions.tailLength / totalLength

        // Create appropriate gradient based on direction
        val brush = createGradientBrush()

        with(drawScope) {
            if (pathIsTailFirst) {
                drawSpeedProgress(progress, tailRatio, brush)
            } else {
                drawRpmProgress(progress, arcRatio, brush)
            }
        }
    }

    /**
     * Draw progress for the speed gauge (tail first, then arc).
     */
    private fun DrawScope.drawSpeedProgress(progress: Float, tailRatio: Float, brush: Brush) {
        if (progress <= tailRatio) {
            // Only on the tail
            val lineProgress = progress / tailRatio
            val tailEnd = Offset(
                dimensions.bottomPoint.x + dimensions.tailLength * lineProgress,
                dimensions.bottomPoint.y
            )
            drawLine(
                brush = brush,
                start = dimensions.bottomPoint,
                end = tailEnd,
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
        } else {
            // Full tail + part of the arc
            drawLine(
                brush = brush,
                start = dimensions.bottomPoint,
                end = dimensions.bottomTailEnd,
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
            val arcProgress = (progress - tailRatio) / (1f - tailRatio)
            drawArc(
                brush = brush,
                startAngle = 90f, // bottom
                sweepAngle = sweepAngle * arcProgress,
                useCenter = false,
                topLeft = dimensions.topLeft,
                size = dimensions.arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }

    /**
     * Draw progress for the RPM gauge (arc first, then tail).
     */
    private fun DrawScope.drawRpmProgress(progress: Float, arcRatio: Float, brush: Brush) {
        if (progress <= arcRatio) {
            // Only on the arc
            val arcProgress = progress / arcRatio
            drawArc(
                brush = brush,
                startAngle = 270f, // top
                sweepAngle = sweepAngle * arcProgress,
                useCenter = false,
                topLeft = dimensions.topLeft,
                size = dimensions.arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        } else {
            // Full arc + part of the tail
            drawArc(
                brush = brush,
                startAngle = 270f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = dimensions.topLeft,
                size = dimensions.arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            val lineProgress = (progress - arcRatio) / (1f - arcRatio)
            val tailEnd = Offset(
                dimensions.bottomPoint.x + dimensions.tailLength * lineProgress * (if (side == HalfSide.SPEED) 1f else -1f),
                dimensions.bottomPoint.y
            )
            drawLine(
                brush = brush,
                start = dimensions.bottomPoint,
                end = tailEnd,
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
        }
    }

    /**
     * Create the appropriate gradient brush based on the side.
     */
    private fun createGradientBrush(): Brush {
        return with(drawScope) {
            when (side) {
                HalfSide.SPEED -> Brush.linearGradient(
                    colors = listOf(progressStart, progressEnd),
                    start = dimensions.topLeft,
                    end = Offset(
                        dimensions.topLeft.x + dimensions.arcSize.width,
                        dimensions.topLeft.y + dimensions.arcSize.height
                    )
                )

                HalfSide.RPM -> Brush.linearGradient(
                    colors = listOf(progressStart, progressEnd),
                    start = Offset(
                        dimensions.topLeft.x + dimensions.arcSize.width,
                        dimensions.topLeft.y
                    ),
                    end = dimensions.topLeft
                )
            }
        }
    }
}

/**
 * Data class to hold all dimension calculations for the ring.
 */
private data class RingDimensions(
    val arcSize: Size,
    val topLeft: Offset,
    val center: Offset,
    val radius: Float,
    val bottomPoint: Offset,
    val bottomTailEnd: Offset,
    val arcLength: Float,
    val tailLength: Float
)