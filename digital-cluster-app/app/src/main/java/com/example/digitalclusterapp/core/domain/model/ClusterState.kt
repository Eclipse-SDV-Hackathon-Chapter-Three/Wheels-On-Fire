package com.example.digitalclusterapp.core.domain.model

/**
 * Represents the state of the vehicle's instrument cluster display.
 *
 * @property speed Current vehicle speed in mph
 * @property rpm Current engine RPM (x1000)
 * @property speedUnit Current vehicle speed unit
 * @property modeTop Top driving mode display text
 * @property modeMid Middle driving mode display text (current mode)
 * @property modeBottom Bottom driving mode display text
 * @property gear Current transmission gear (P, R, N, D)
 * @property ambientTempC Ambient temperature in Celsius
 * @property economy Current fuel economy
 */
data class ClusterState(
    val typeOfVehicle: VehicleType = VehicleType.ELECTRIC,
    val gear: Char = 'D',
    val speedUnit: String = "mph",
    val speed: Int = 0,
    val cruiseControl: Boolean = false,
    val cruiseControlSpeed: Int = 0,
    val rpm: Float = 0f,
    val battery: Int = 0,
    val rangeRemaining: Int = 0,
    val tempUnit: Int = 0,
    val engineTemp: Float = 0f,
    val economy: String = "11.6 km/L",
    val ambientTempC: Int = 25,
    val location: Boolean = false,
    val currentCentralScreen: CentralScreenState = CentralScreenState.MODES,
    val modeTop: String = "Race",
    val modeMid: String = "Sport+",
    val modeBottom: String = "City"
)