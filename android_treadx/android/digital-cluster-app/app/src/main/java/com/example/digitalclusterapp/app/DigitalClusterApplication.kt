package com.example.digitalclusterapp.app

import android.app.Application
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.digitalclusterapp.R

/**
 * Main application class for the Digital Cluster app.
 * Optimized with resource preloading.
 */
@HiltAndroidApp
class DigitalClusterApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Preload critical resources in a background thread
        applicationScope.launch {
            preloadDrawableResources()
        }
    }

    /**
     * Preloads frequently used drawable resources to improve rendering performance
     */
    private suspend fun preloadDrawableResources() {
        val resourceIds = listOf(
            R.drawable.ic_background,
            R.drawable.ic_speedometer_gauge,
            R.drawable.ic_speedometer_ellipse,
            R.drawable.ic_rpm_gauge,
            R.drawable.top_icon_bar,
            R.drawable.ic_map,
            R.drawable.ic_cruise_control_active,
            R.drawable.ic_cruise_control_default
        )

        withContext(Dispatchers.IO) {
            resourceIds.forEach { resourceId ->
                try {
                    ResourcesCompat.getDrawable(resources, resourceId, theme)
                } catch (e: Exception) {
                    Log.e("ResourcePreload", "Failed to preload resource: $resourceId", e)
                }
            }
        }

        Log.d("ResourcePreload", "Preloaded ${resourceIds.size} drawable resources")
    }
}