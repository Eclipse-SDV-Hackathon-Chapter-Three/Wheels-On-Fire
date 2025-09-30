package com.example.digitalclusterapp.feature.cluster.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.domain.action.ClusterAction
import com.example.digitalclusterapp.core.domain.model.ClusterState
import com.example.digitalclusterapp.feature.cluster.ui.component.ClusterBottomStatusBar
import com.example.digitalclusterapp.feature.cluster.ui.component.ClusterGearIndicator
import com.example.digitalclusterapp.feature.cluster.ui.component.ClusterMiddleDisplay
import com.example.digitalclusterapp.feature.cluster.ui.component.ClusterRightDisplay
import com.example.digitalclusterapp.feature.cluster.ui.component.ClusterSpeedDisplay
import com.example.digitalclusterapp.feature.cluster.ui.component.ClusterTopBar
import com.example.digitalclusterapp.feature.cluster.ui.component.ControlButtons

/**
 * A modern automotive instrument cluster UI component.
 * Optimized for performance with BoxWithConstraints and remember.
 *
 * @param state The current state of the cluster display
 * @param onAction Callback for user actions
 * @param modifier Modifier to be applied to the component
 * @param aspectRatio The width to height ratio of the component
 * @param cornerRadius The corner radius of the cluster display
 */
@Composable
fun Cluster(
    modifier: Modifier = Modifier,
    state: ClusterState,
    onAction: (ClusterAction) -> Unit,
    aspectRatio: Float = 964f / 373f,
    cornerRadius: Dp = 180.dp
) {
    // Get background painter directly in the composable context
    val backgroundPainter = painterResource(id = R.drawable.ic_background)

    // Get local density for dp to px conversions
    val density = LocalDensity.current

    // Calculate offset with proper density conversion
    val offsetX = with(density) { 12.dp.toPx() }.toInt()
    val speedDisplayOffset = IntOffset(x = offsetX, y = 0)

    // Use BoxWithConstraints to calculate dimensions once
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(cornerRadius))
            .padding(20.dp)
    ) {
        val width = maxWidth
        val height = maxHeight

        // Calculate positions once based on constraints
        val topBarWidth = remember(width) { minOf(width * 0.9f, 1100.dp) }

        // Background
        Image(
            modifier = Modifier.matchParentSize(),
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        // Central mode display with top icons
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(top = 37.dp, start = 620.dp, end = 338.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClusterTopBar(
                modifier = Modifier.width(topBarWidth),
                cruiseControl = state.cruiseControl
            )
        }

//        // Central display (modes, map, or sensors)
//        ClusterMiddleDisplay(
//            modifier = Modifier
//                .align(Alignment.Center)
//                .padding(top = 6.dp),
//            current = state.currentCentralScreen
//        )

        // Speed display
        ClusterSpeedDisplay(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 27.dp)
                .offset { speedDisplayOffset },
            speed = state.speed,
            cruiseControl = state.cruiseControl,
            speedUnit = state.speedUnit,
            cruiseControlSpeed = state.cruiseControlSpeed
        )

        // Right Side Display (RPM or Battery)
//        ClusterRightDisplay(
//            modifier = Modifier.align(Alignment.CenterEnd),
//            state = state
//        )
//
//        // Bottom status bar
//        ClusterBottomStatusBar(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .width(900.dp)
//                .padding(bottom = 17.dp),
//            economy = state.economy,
//        )

//        // Gear indicator
//        ClusterGearIndicator(
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(bottom = 256.dp, end = 609.dp),
//            state = state
//        )
    }

    // Control buttons
    Box(Modifier.padding(top = 90.dp)) {
        ControlButtons(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 20.dp)
                .width(500.dp),
            onAction = onAction,
            state = state
        )
    }
}

@Preview(widthDp = 2000, heightDp = 818)
@Composable
fun ClusterPreview() {
    Cluster(
        modifier = Modifier.fillMaxSize(),
        state = ClusterState(),
        onAction = { /* Preview Only */ }
    )
}