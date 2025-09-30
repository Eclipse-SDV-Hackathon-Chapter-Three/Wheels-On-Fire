package com.example.digitalclusterapp.core.data.remote.mqtt

import android.util.Log
import com.example.digitalclusterapp.core.domain.model.ClusterState
import com.example.digitalclusterapp.core.domain.model.VehicleType
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Connects messages from MQTT/uProtocol to the ClusterState.
 * Supports two message formats:
 * 1. JSON format from ClusterDataInjector (primary)
 * 2. Legacy key-value format like "speed-100" (backward compatibility)
 *
 * Performance optimized with message batching and efficient state updates.
 *
 * @param scope CoroutineScope for launching asynchronous operations
 */
class MqttClusterBinder(
    private val scope: CoroutineScope
) {

    companion object {
        private const val TAG = "MqttClusterBinder"
        private const val BATCH_SIZE = 10
        private const val BATCH_TIMEOUT_MS = 50L

        // MQTT Configuration
        private const val BROKER_HOST = "10.0.2.2" // Replace with your broker host
        private const val BROKER_PORT = 1883 // Default MQTT port
        private const val CLIENT_ID_PREFIX = "AndroidCluster-"
        private const val TOPIC = "vehicle/parameters" // Replace with your topic
    }

    init {
        start()
    }

    // MQTT Client
    private var mqttClient: Mqtt5AsyncClient? = null

    private val _state = MutableStateFlow(ClusterState())

    /**
     * Observable state flow of the cluster display
     */
    val state: StateFlow<ClusterState> = _state.asStateFlow()


    // Channel for message batching
    private val messageQueue = Channel<String>(Channel.BUFFERED)

    /**
     * Starts listening for MQTT messages and updates the cluster state accordingly
     */
    fun start() {
        // Initialize and connect MQTT client
        initializeMqttClient()

        // Process messages in batches
        scope.launch(Dispatchers.IO) {
            val batch = mutableListOf<String>()
            while (isActive) {
                // Collect messages for a short time or until batch size reached
                val message = withTimeoutOrNull(BATCH_TIMEOUT_MS) { messageQueue.receive() }
                if (message != null) {
                    batch.add(message)

                    // Collect any additional pending messages
                    while (batch.size < BATCH_SIZE && messageQueue.tryReceive().getOrNull()?.let { batch.add(it) } != null) {}

                    // Process the batch
                    processBatch(batch)
                    batch.clear()
                }
            }
        }
    }

    /**
     * Handles incoming MQTT messages
     */
    private fun handleIncomingMessage(publish: Mqtt5Publish) {
        val topic = publish.topic.toString()
        val payload = publish.payloadAsBytes

        if (payload != null) {
            val message = String(payload, StandardCharsets.UTF_8)
            Log.d(TAG, "Message received on topic $topic: $message")
            messageQueue.trySend(message)
        }
    }

    private fun initializeMqttClient() {
        val clientId = CLIENT_ID_PREFIX + UUID.randomUUID().toString()

        try {
            // Build MQTT5 client
            val client = Mqtt5Client.builder()
                .identifier(clientId)
                .serverHost(BROKER_HOST)
                .serverPort(BROKER_PORT)
                .automaticReconnect()
                .initialDelay(1, TimeUnit.SECONDS)
                .maxDelay(120, TimeUnit.SECONDS)
                .applyAutomaticReconnect()
                .buildAsync()

            Log.d(TAG, "Connecting to MQTT broker at $BROKER_HOST:$BROKER_PORT")

            // Connect with proper callback handling
            client.connectWith()
                .cleanStart(true)
                .send()
                .whenComplete { connAck, throwable ->
                    if (throwable != null) {
                        // Connection failed
                        Log.e(TAG, "Connection failed: ${throwable.message}", throwable)
                    } else {
                        // Connection successful
                        Log.d(TAG, "Connected successfully to MQTT broker: ${connAck.reasonCode}")

                        // Now that we're connected, subscribe to the topic
                        client.subscribeWith()
                            .topicFilter(TOPIC)
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .send()
                            .whenComplete { subAck, subThrowable ->
                                if (subThrowable != null) {
                                    Log.e(TAG, "Failed to subscribe: ${subThrowable.message}", subThrowable)
                                } else {
                                    Log.d(TAG, "Successfully subscribed to topic: $TOPIC")
                                }
                            }

                        // Set up callback for incoming messages
                        client.toAsync().publishes(MqttGlobalPublishFilter.ALL) { publish ->
                            handleIncomingMessage(publish)
                        }
                    }
                }

            mqttClient = client
            Log.d(TAG, "MQTT5 client initialization completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MQTT5 client", e)
        }
    }

    /**
     * Updates the cluster state with a new state
     *
     * @param newState The new state to set
     */
    fun updateState(newState: ClusterState) {
        _state.value = newState
    }

    /**
     * Process a batch of messages and update state only once
     */
    private fun processBatch(messages: List<String>) {
        if (messages.isEmpty()) return

        var current = _state.value
        var next = current
        var changed = false

        for (msg in messages) {
            Log.d(TAG, "Processing message: $msg")

            // Try to parse as JSON first (ClusterDataInjector format)
            val jsonData = runCatching { JSONObject(msg) }.getOrNull()
            if (jsonData != null) {
                val result = processJsonMessage(jsonData)
                next = result.first
                changed = changed || result.second
                continue
            }

            // Fallback to legacy key-value format for backward compatibility
            val pairs = parsePairs(msg)
            if (pairs.isNotEmpty()) {
                val result = processKeyValueMessage(pairs, next)
                next = result.first
                changed = changed || result.second
            }
        }

        // Only update state if something changed
        if (changed) {
            _state.value = next
            Log.d(TAG, "Updated state after batch processing: $next")
        }
    }

    /**
     * Processes a JSON message and returns the updated state and whether it changed
     */
    private fun processJsonMessage(jsonData: JSONObject): Pair<ClusterState, Boolean> {
        var current = _state.value
        var next = current
        var changed = false

        try {
            // Map JSON fields to ClusterState properties with change tracking
            jsonData.optInt("Speed", current.speed).let {
                if (it != current.speed) {
                    next = next.copy(speed = it)
                    changed = true
                }
            }

            jsonData.optBoolean("CruiseControl", current.cruiseControl).let {
                if (it != current.cruiseControl) {
                    next = next.copy(cruiseControl = it)
                    changed = true
                }
            }

            jsonData.optDouble("RPM", current.rpm.toDouble()).toFloat().let {
                if (it != current.rpm) {
                    next = next.copy(rpm = it)
                    changed = true
                }
            }

            jsonData.optInt("Engine Temperature", current.engineTemp.toInt()).toFloat().let {
                if (it != current.engineTemp) {
                    next = next.copy(engineTemp = it)
                    changed = true
                }
            }

            jsonData.optString("Gear", current.gear.toString()).firstOrNull()?.let {
                if (it != current.gear) {
                    next = next.copy(gear = it)
                    changed = true
                }
            }

            jsonData.optInt("AmbientTemperature", current.ambientTempC).let {
                if (it != current.ambientTempC) {
                    next = next.copy(ambientTempC = it)
                    changed = true
                }
            }

            jsonData.optString("Economy", current.economy).let {
                if (it != current.economy) {
                    next = next.copy(economy = it)
                    changed = true
                }
            }

            jsonData.optString("SpeedUnit", current.speedUnit).let {
                if (it != current.speedUnit) {
                    next = next.copy(speedUnit = it)
                    changed = true
                }
            }

            jsonData.optInt("Battery", current.battery).let {
                if (it != current.battery) {
                    next = next.copy(battery = it)
                    changed = true
                }
            }

            jsonData.optInt("Range", current.rangeRemaining).let {
                if (it != current.rangeRemaining) {
                    next = next.copy(rangeRemaining = it)
                    changed = true
                }
            }

            jsonData.optInt("TemperatureUnit", current.tempUnit).let {
                if (it != current.tempUnit) {
                    next = next.copy(tempUnit = it)
                    changed = true
                }
            }

            jsonData.optBoolean("ShareLocation", current.location).let {
                if (it != current.location) {
                    next = next.copy(location = it)
                    changed = true
                }
            }

            // Handle TypeOfVehicle (0 = Petrol/Combust, 1 = Electric)
            val vehicleTypeInt = jsonData.optInt("TypeOfVehicle", -1)
            if (vehicleTypeInt != -1) {
                val vehicleType = if (vehicleTypeInt == 1) VehicleType.ELECTRIC else VehicleType.COMBUST
                if (vehicleType != current.typeOfVehicle) {
                    next = next.copy(typeOfVehicle = vehicleType)
                    changed = true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON message", e)
        }

        return Pair(next, changed)
    }

    /**
     * Processes a key-value message and returns the updated state and whether it changed
     */
    private fun processKeyValueMessage(pairs: Map<String, String>, current: ClusterState): Pair<ClusterState, Boolean> {
        var next = current
        var changed = false

        pairs.forEach { (key, rawValue) ->
            when (key.lowercase()) {
                "speed" -> rawValue.toIntOrNull()?.let {
                    if (it != current.speed) {
                        next = next.copy(speed = it)
                        changed = true
                    }
                }

                "cruisecontrol" -> {
                    val boolValue = rawValue.toBoolean()
                    if (boolValue != current.cruiseControl) {
                        next = next.copy(cruiseControl = boolValue)
                        changed = true
                    }
                }

                "rpm" -> rawValue.toFloatOrNull()?.let {
                    if (it != current.rpm) {
                        next = next.copy(rpm = it)
                        changed = true
                    }
                }

                "enginetemp" -> rawValue.toFloatOrNull()?.let {
                    if (it != current.engineTemp) {
                        next = next.copy(engineTemp = it)
                        changed = true
                    }
                }

                "mode", "modetop" -> {
                    if (rawValue != current.modeTop) {
                        next = next.copy(modeTop = rawValue)
                        changed = true
                    }
                }

                "modemid" -> {
                    if (rawValue != current.modeMid) {
                        next = next.copy(modeMid = rawValue)
                        changed = true
                    }
                }

                "modebottom" -> {
                    if (rawValue != current.modeBottom) {
                        next = next.copy(modeBottom = rawValue)
                        changed = true
                    }
                }

                "gear" -> rawValue.firstOrNull()?.let {
                    if (it != current.gear) {
                        next = next.copy(gear = it)
                        changed = true
                    }
                }

                "ambienttempc", "ambient", "temp", "tempc" ->
                    rawValue.toIntOrNull()?.let {
                        if (it != current.ambientTempC) {
                            next = next.copy(ambientTempC = it)
                            changed = true
                        }
                    }

                "economy" -> {
                    if (rawValue != current.economy) {
                        next = next.copy(economy = rawValue)
                        changed = true
                    }
                }
            }
        }
        return Pair(next, changed)
    }


    /**
     * Parses text into key-value pairs
     * Accepts formats like: speed-100, speed:100, speed = 100, speed 100
     *
     * @param text The text to parse
     * @return Map of key-value pairs
     */
    private fun parsePairs(text: String): Map<String, String> {
        Log.d(TAG, "Parsing message: $text")

        val result = mutableMapOf<String, String>()
        val chunks = text.split('\n', ',', ';')
        val regex = Regex("""^\s*([A-Za-z_][\w]*)\s*[-:=\s]\s*([A-Za-z0-9\.\+\-]+)\s*$""")

        for (c in chunks) {
            val m = regex.find(c) ?: continue
            val key = m.groupValues[1]
            val value = m.groupValues[2]
            result[key] = value
        }

        Log.d(TAG, "Parsed result: $result")
        return result
    }

    suspend fun publishClusterStateAsJson(clusterState: ClusterState): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("TypeOfVehicle", if (clusterState.typeOfVehicle == VehicleType.ELECTRIC) 1 else 0)
                    put("Gear", clusterState.gear.toString())
                    put("CruiseControl", clusterState.cruiseControl)
                    put("ShareLocation", clusterState.location)
                    put("Speed", clusterState.speed)
                    put("SpeedUnit", clusterState.speedUnit)
                    put("RPM", clusterState.rpm)
                    put("Economy", clusterState.economy)
                    put("AmbientTemperature", clusterState.ambientTempC)
                    put("EngineTemperature", clusterState.engineTemp.toInt())
                    put("TemperatureUnit", clusterState.tempUnit)
                    put("Battery", clusterState.battery)
                    put("Range", clusterState.rangeRemaining)
                    put("ModeTop", clusterState.modeTop)
                    put("ModeMid", clusterState.modeMid)
                    put("ModeBottom", clusterState.modeBottom)
                    put("CentralScreen", clusterState.currentCentralScreen.ordinal)
                }

                val jsonString = json.toString()
                Log.d(TAG, "Publishing ClusterState as JSON: $jsonString")
                return@withContext publishMessage(TOPIC,jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating JSON from ClusterState", e)
                return@withContext false
            }
        }


    /**
     * Publishes a message to the MQTT broker
     *
     * @param topic The topic to publish to
     * @param message The message to publish
     * @param qos The quality of service level (0, 1, or 2)
     * @param retain Whether the message should be retained
     */
    fun publishMessage(
        topic: String,
        message: String,
    ) : Boolean {
        try {
            mqttClient?.publishWith()
                ?.topic(topic)
                ?.payload(message.toByteArray())
                ?.send()
            Log.d(TAG, "Message published to topic: $topic")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error publishing message", e)
        }
        return false
    }
}