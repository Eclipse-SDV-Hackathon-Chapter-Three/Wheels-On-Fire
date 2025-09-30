package com.example.digitalclusterapp.core.data.repository

import com.example.digitalclusterapp.core.data.remote.mqtt.MqttClusterBinder
import com.example.digitalclusterapp.core.domain.model.ClusterState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class MqttClusterRepositoryImpl @Inject constructor(
    private val binder: MqttClusterBinder,
) : MqttClusterRepository {

    // Combine both uProtocol and direct MQTT state flows
    private val _combinedState = MutableStateFlow(ClusterState())
    override val state: StateFlow<ClusterState> = _combinedState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Collect from both flows and update the combined state
        scope.launch {
                binder.state.collect { state ->
                _combinedState.value = state
            }
        }
    }

    override suspend fun updateSpeed(speed: Int): Boolean {
        // Get current state and update only the speed
        val currentState = state.value
        val updatedState = currentState.copy(speed = speed)
        return binder.publishClusterStateAsJson(updatedState)
    }

    override suspend fun updateRpm(rpm: Int): Boolean {
        val currentState = state.value
        val updatedState = currentState.copy(rpm = rpm.toFloat())
        return binder.publishClusterStateAsJson(updatedState)
    }

    override suspend fun toggleCruiseControl(): Boolean {
        val currentState = state.value
        val updatedState = currentState.copy(cruiseControl = !currentState.cruiseControl)
        return binder.publishClusterStateAsJson(updatedState)
    }

    override suspend fun toggleLocation(): Boolean {
        val currentState = state.value
        val updatedState = currentState.copy(location = !currentState.location)
        return binder.publishClusterStateAsJson(updatedState)
    }

    override suspend fun updateClusterState(newState: ClusterState): Boolean {
        // Update the local state through the binder
        binder.updateState(newState)
        // Also publish the complete state to MQTT
        return binder.publishClusterStateAsJson(newState)
    }

    /**
     * Updates a specific property of the cluster state and publishes the entire state
     *
     * @param updateFunction A function that takes the current state and returns an updated state
     * @return true if publishing was successful, false otherwise
     */
    suspend fun updateAndPublishState(updateFunction: (ClusterState) -> ClusterState): Boolean {
        val currentState = state.value
        val updatedState = updateFunction(currentState)
        return binder.publishClusterStateAsJson(updatedState)
    }
}