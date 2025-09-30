package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography

/**
 * Displays a map in the central display area.
 * Optimized for performance.
 *
 * @param modifier Modifier for the component
 */
@Composable
fun ClusterMapDisplay(
    modifier: Modifier = Modifier
) {
    // Get painter directly in the composable context
    val mapPainter = painterResource(id = R.drawable.ic_map)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(width = 668.dp, height = 521.dp)
                .padding(start = 90.dp),
            painter = mapPainter,
            contentDescription = "Navigation Map",
            contentScale = ContentScale.Crop
        )
    }
}