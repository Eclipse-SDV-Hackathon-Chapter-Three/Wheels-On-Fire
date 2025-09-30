package com.example.digitalclusterapp.feature.cluster.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.digitalclusterapp.core.domain.model.ClusterState
import com.example.digitalclusterapp.core.domain.model.VehicleType

@Composable
fun ClusterRightDisplay(
    modifier: Modifier = Modifier,
    state: ClusterState
) {

    when (state.typeOfVehicle) {
        VehicleType.COMBUST ,
//            ClusterRpmDisplay(
//                modifier = modifier
//                    .padding(end = 100.dp),
//                rpm = state.rpm,
//                engineTemp = state.engineTemp,
////                tempUnit = state.tempUnit
//            )
        //TODO -> Create the electric
        VehicleType.ELECTRIC ->
            ClusterBatteryDisplay(
                modifier = modifier
                    .padding(end = 100.dp),
                chargeLevel = state.battery.toFloat()/100,
                range = state.rangeRemaining.toFloat(),
                rangeUnit = "km"
            )
    }
}