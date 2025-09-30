package com.example.digitalclusterapp.feature.cluster.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitalclusterapp.feature.cluster.viewmodel.ClusterViewModel

/**
 * Main screen for the digital cluster display.
 *
 * @param viewModel ViewModel that provides the cluster state
 */
@Composable
fun ClusterScreen(viewModel: ClusterViewModel = viewModel()) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    Cluster(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onAction = { action -> viewModel.handleAction(action) }
    )
}