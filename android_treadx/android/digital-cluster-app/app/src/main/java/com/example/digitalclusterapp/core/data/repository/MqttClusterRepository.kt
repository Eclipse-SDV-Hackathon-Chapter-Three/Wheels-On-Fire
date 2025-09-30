package com.example.digitalclusterapp.core.data.repository

import com.example.digitalclusterapp.core.domain.model.ClusterState
import kotlinx.coroutines.flow.StateFlow

interface MqttClusterRepository {
    val state: StateFlow<ClusterState>

    suspend fun updateSpeed(speed: Int): Boolean
    suspend fun updateRpm(rpm: Int): Boolean
    suspend fun toggleCruiseControl(): Boolean
    suspend fun toggleLocation(): Boolean
    suspend fun updateClusterState(newState: ClusterState): Boolean
}