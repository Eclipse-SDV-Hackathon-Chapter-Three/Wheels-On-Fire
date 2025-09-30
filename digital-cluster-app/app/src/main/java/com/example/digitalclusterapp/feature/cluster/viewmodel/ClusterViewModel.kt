package com.example.digitalclusterapp.feature.cluster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalclusterapp.core.data.repository.MqttClusterRepository
import com.example.digitalclusterapp.core.domain.action.ClusterAction
import com.example.digitalclusterapp.core.domain.model.CentralScreenState
import com.example.digitalclusterapp.core.domain.model.ClusterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the cluster display.
 * Manages the state of the cluster and handles business logic.
 * Optimized with action debouncing and state caching.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class ClusterViewModel @Inject constructor(
    private val repository: MqttClusterRepository
) : ViewModel() {

    // Cache the latest state to avoid unnecessary repository calls
    private var cachedState: ClusterState? = null

    /**
     * Current state of the cluster display.
     */
    val state: StateFlow<ClusterState> = repository.state
        .onEach { cachedState = it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ClusterState())

    /**
     * Action handler with debouncing for rapid inputs
     */
    private val actionHandler = MutableSharedFlow<ClusterAction>(extraBufferCapacity = 10)

    init {
        viewModelScope.launch {
            actionHandler
                .debounce(50) // Debounce rapid inputs
                .collect { action ->
                    processAction(action)
                }
        }
    }

    /**
     * Updates the cluster state for testing or manual control.
     */
    fun updateClusterState(newState: ClusterState) {
        viewModelScope.launch {
            repository.updateClusterState(newState)
        }
    }

    /**
     * Handles cluster actions
     */
    fun handleAction(action: ClusterAction) {
        actionHandler.tryEmit(action)
    }

    /**
     * Processes actions after debouncing
     */
    private fun processAction(action: ClusterAction) {
        when (action) {
            is ClusterAction.Speed.Increase -> increaseSpeed()
            is ClusterAction.Speed.Decrease -> decreaseSpeed()
            is ClusterAction.ToggleCruiseControl -> toggleCruiseControl()
            is ClusterAction.ToggleLocation -> toggleLocation()
            is ClusterAction.ParkingSensor.Front -> frontParkingSensor()
            is ClusterAction.ParkingSensor.Rear -> rearParkingSensor()
            is ClusterAction.ParkingSensor.Left -> leftParkingSensor()
            is ClusterAction.ParkingSensor.Right -> rightParkingSensor()
        }
    }

    /**
     * Increases speed by the specified increment and publishes the new value
     * @param increment Amount to increase speed by (default: 10)
     */
    private fun increaseSpeed(increment: Int = 10) {
        val currentSpeed = state.value.cruiseControlSpeed
        val newSpeed = minOf(currentSpeed + increment, 220)

        viewModelScope.launch {
            repository.updateSpeed(newSpeed)
        }
    }

    /**
     * Decreases speed by the specified decrement and publishes the new value
     * @param decrement Amount to decrease speed by (default: 10)
     */
    private fun decreaseSpeed(decrement: Int = 10) {
        val currentSpeed = state.value.cruiseControlSpeed
        val newSpeed = maxOf(currentSpeed - decrement, 0)

        viewModelScope.launch {
            repository.updateSpeed(newSpeed)
        }
    }

    /**
     * Toggles the cruise control state
     */
    private fun toggleCruiseControl() {
        viewModelScope.launch {
            repository.toggleCruiseControl()
        }
    }

    /**
     * Toggles the location state
     */
    private fun toggleLocation() {
        viewModelScope.launch {
            repository.toggleLocation()
        }
    }

    /**
     * Toggles the Front Parking Sensor state
     */
    private fun frontParkingSensor() {
        val currentState = state.value
        updateClusterState(currentState.copy(currentCentralScreen = CentralScreenState.SENSORS_FORWARD))
    }

    /**
     * Toggles the Rear Parking Sensor state
     */
    private fun rearParkingSensor() {
        val currentState = state.value
        updateClusterState(currentState.copy(currentCentralScreen = CentralScreenState.SENSORS_FORWARD))
    }

    /**
     * Toggles the Left Parking Sensor state
     */
    private fun leftParkingSensor() {
        val currentState = state.value
        updateClusterState(currentState.copy(currentCentralScreen = CentralScreenState.SENSORS_BLIND))
    }

    /**
     * Toggles the Right Parking Sensor state
     */
    private fun rightParkingSensor() {
        val currentState = state.value
        updateClusterState(currentState.copy(currentCentralScreen = CentralScreenState.MODES))
    }
}