package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.R
import com.example.digitalclusterapp.core.designsystem.theme.LocalClusterTypography

@Composable
fun ClusterSensorCollisionDisplay(
    modifier: Modifier = Modifier
) {
    val typography = LocalClusterTypography.current
    val frontSensor = painterResource(id = R.drawable.ic_front_radar)
    val car = painterResource(id = R.drawable.ic_car)
    val backSensor = painterResource(id = R.drawable.ic_rear_radar)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(width = 88.dp, height = 243.dp)
                .offset(x = (20).dp)           ,
            painter = frontSensor,
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
                .size(width = 88.dp, height = 243.dp),
            painter = backSensor,
            contentDescription = "Rear Radar",
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(widthDp = 2000, heightDp = 818)
@Composable
fun ClusterSensorCollisionDisplayPreview() {
    ClusterSensorCollisionDisplay(
    )
}