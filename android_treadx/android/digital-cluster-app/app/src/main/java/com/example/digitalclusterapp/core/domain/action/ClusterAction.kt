package com.example.digitalclusterapp.core.domain.action

sealed class ClusterAction {
    // Speed-related actions
    sealed class Speed : ClusterAction() {
        object Increase : Speed()
        object Decrease : Speed()
    }

    // Cruise control action
    object ToggleCruiseControl : ClusterAction()

    // Location action
    object ToggleLocation : ClusterAction()

    // Parking sensor actions
    sealed class ParkingSensor : ClusterAction() {
        object Front : ParkingSensor()
        object Rear : ParkingSensor()
        object Left : ParkingSensor()
        object Right : ParkingSensor()
    }
}