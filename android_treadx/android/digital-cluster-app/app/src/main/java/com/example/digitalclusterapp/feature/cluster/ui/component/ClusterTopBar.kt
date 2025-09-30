package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R

/**
 * Data class representing a status indicator in the top bar.
 *
 * @property drawableRes Resource ID for the indicator icon
 * @property tint Color tint to apply to the icon
 * @property active Whether the indicator is active
 * @property visible Whether the indicator is visible
 */
data class ClusterIndicator(
    @DrawableRes val drawableRes: Int,
    val tint: Color = Color.White,
    val active: Boolean = false,
    val visible: Boolean = false
)

/**
 * Displays a row of status indicators in the top bar.
 * Optimized to prevent unnecessary recompositions.
 *
 * @param modifier Modifier for the component
 * @param iconSpacing Spacing between icons
 * @param cruiseControl Whether cruise control is active
 */
@Composable
fun ClusterTopBar(
    modifier: Modifier = Modifier,
    iconSpacing: Dp = 25.dp,
    cruiseControl: Boolean = false
) {
    // Remember indicators list only when cruise control changes
    val indicators = remember(cruiseControl) {
        defaultIndicators(cruiseControl)
    }

    // Get background painter directly in the composable context
    val backgroundPainter = painterResource(R.drawable.top_icon_bar)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(RoundedCornerShape(45.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = "Status bar background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(iconSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            indicators.forEach { indicator ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    if (indicator.visible) {
                        // Get icon painter directly in the composable context
                        val iconPainter = painterResource(id = indicator.drawableRes)

                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = iconPainter,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Returns the default set of cluster indicators.
 * Optimized to create indicators only when cruise control state changes.
 */
fun defaultIndicators(cruiseControl: Boolean): List<ClusterIndicator> {
    return listOf(
        ClusterIndicator(drawableRes = R.drawable.ic_left_blinker, active = false),
        ClusterIndicator(drawableRes = R.drawable.ic_engine, tint = Color.Yellow),
        ClusterIndicator(
            drawableRes = if (cruiseControl) R.drawable.ic_cruise_control_active
            else R.drawable.ic_cruise_control_default,
            visible = true
        ),
        ClusterIndicator(
            drawableRes = R.drawable.ic_battery,
            tint = Color(0x0062B1FF),
            active = true
        ),
        ClusterIndicator(drawableRes = R.drawable.ic_oil_pressure),
        ClusterIndicator(
            drawableRes = R.drawable.ic_lights_active,
            tint = Color(0x000A203D),
            active = true
        ),
        ClusterIndicator(
            drawableRes = R.drawable.ic_max_lights,
            tint = Color(0xFF0A203D),
            active = true,
        ),
        ClusterIndicator(drawableRes = R.drawable.ic_low_beam),
        ClusterIndicator(drawableRes = R.drawable.ic_seatbelt),
        ClusterIndicator(
            drawableRes = R.drawable.ic_brake,
            tint = Color(0xFF0A203D),
            active = true
        ),
        ClusterIndicator(drawableRes = R.drawable.ic_right_blinker)
    )
}