package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography
import com.example.digitalclusterapp.core.domain.model.ClusterState
import com.example.digitalclusterapp.feature.cluster.ui.screen.Cluster

@Composable
fun ClusterSensorBlindDisplay(
    modifier: Modifier = Modifier
) {
    val typography = LocalClusterTypography.current
    val leftRadar = painterResource(id = R.drawable.ic_left_radar)
    val car = painterResource(id = R.drawable.ic_car)
    val rightRadar = painterResource(id = R.drawable.ic_right_radar)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(width = 84.dp, height = 34.dp)
                .offset(x = (-40).dp),
            painter = rightRadar,
            contentDescription = "Front Radar",
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier
                .size(width = 400.dp, height = 212.dp),
            painter = car,
            contentDescription = "Car Image",
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier
                .size(width = 84.dp, height = 34.dp)
                .offset(x = (-40).dp),
            painter = leftRadar,
            contentDescription = "Rear Radar",
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(widthDp = 2000, heightDp = 818)
@Composable
fun ClusterSensorBlindDisplayPreview() {
    ClusterSensorBlindDisplay(
    )
}