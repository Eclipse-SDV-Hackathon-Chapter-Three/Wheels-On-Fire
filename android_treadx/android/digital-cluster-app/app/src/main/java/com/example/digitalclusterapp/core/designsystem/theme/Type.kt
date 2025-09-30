package com.example.digitalclusterapp.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.digitalclusterapp.R

/**
 * Typography styles for the Digital Cluster application.
 */
object ClusterTypography {
    private val clusterFont = FontFamily(
        Font(R.font.anta_regular, FontWeight.Normal)
    )

    val mainSpeed = TextStyle(
        fontFamily = clusterFont,
        fontSize = 110.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val mainRPM = TextStyle(
        fontFamily = clusterFont,
        fontSize = 50.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val subSpeedRpm = TextStyle(
        fontFamily = clusterFont,
        fontSize = 33.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )
    val temperatureText = TextStyle(
        fontFamily = clusterFont,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val batteryChargeText = TextStyle(
        fontFamily = clusterFont,
        fontSize = 130.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val batteryChargePercentageText = TextStyle(
        fontFamily = clusterFont,
        fontSize = 50.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val rangeValueText = TextStyle(
        fontFamily = clusterFont,
        fontSize = 50.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val mainCenter = TextStyle(
        fontFamily = clusterFont,
        fontSize = 85.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val subCenter = TextStyle(
        fontFamily = clusterFont,
        fontSize = 50.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.InactiveText
    )

    val mainTempGas = TextStyle(
        fontFamily = clusterFont,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.ClusterInfoBlue
    )

    val mainGear = TextStyle(
        fontFamily = clusterFont,
        fontSize = 40.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.TextWhite
    )

    val subGear = TextStyle(
        fontFamily = clusterFont,
        fontSize = 40.sp,
        fontWeight = FontWeight.Normal,
        color = ClusterColors.InactiveText
    )
}