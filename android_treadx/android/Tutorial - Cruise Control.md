# Adding a New Feature to Digital Cluster App: Cruise Control Tutorial

This tutorial demonstrates how to add a new feature to the Digital Cluster App using cruise control as an example.

## Step 1: Update the Domain Model

Add the new property to the `ClusterState` data class:

```kotlin
// In core/domain/model/ClusterState.kt
data class ClusterState(
    // Existing properties...
    val cruiseControl: Boolean = false, // Add the new property
    // Other properties...
)
```

## Step 2: Add an Action for the Feature
(optional if need to have user interaction)

If your feature needs user interaction, create an action:

```kotlin
// In core/domain/action/ClusterAction.kt
sealed class ClusterAction {
    // Existing actions...
    object ToggleCruiseControl : ClusterAction()
}
```

## Step 3: Update the Repository Interface
(optional if need to have user interaction)

Add a method to toggle the feature:

```kotlin
// In core/data/repository/MqttClusterRepository.kt
interface MqttClusterRepository {
    // Existing methods...
    suspend fun toggleCruiseControl(): Boolean
}
```

## Step 4: Implement the Repository Method 
(optional if need to have user interaction)

Implement the toggle method:

```kotlin
// In core/data/repository/MqttClusterRepositoryImpl.kt
override suspend fun toggleCruiseControl(): Boolean {
    val currentState = state.value
    val updatedState = currentState.copy(cruiseControl = !currentState.cruiseControl)
    return publisher.publishClusterStateAsJson(updatedState)
}
```

## Step 5: Update the MQTT Message Handlers

Ensure message handlers can process the new feature:

```kotlin
// In MqttClusterBinder.kt - JSON format
jsonData.optBoolean("CruiseControl", current.cruiseControl).let {
    if (it != current.cruiseControl) {
        next = next.copy(cruiseControl = it)
        changed = true
    }
}

// In MqttClusterBinder.kt - Key-value format
when (key.lowercase()) {
    // Existing cases...
    "cruisecontrol" -> {
        val boolValue = rawValue.toBoolean()
        if (boolValue != current.cruiseControl) {
            next = next.copy(cruiseControl = boolValue)
            changed = true
        }
    }
}
```

## Step 6: Add UI Assets

Add cruise control icons to the drawable resources:

- `ic_cruise_control_default.xml` - Icon for inactive state
- `ic_cruise_control_active.xml` - Icon for active state

## Step 7: Update the Top Bar to Display the Icon
(or where you want the icon to appear)

Modify the `ClusterTopBar` component:

```kotlin
// In ClusterTopBar.kt
@Composable
fun ClusterTopBar(
    modifier: Modifier = Modifier,
    iconSpacing: Dp = 25.dp,
    cruiseControl: Boolean = false  // Add parameter
) {
    // Remember indicators list only when cruise control changes
    val indicators = remember(cruiseControl) {
        defaultIndicators(cruiseControl)
    }
    
    // Existing code...
}

// Update the indicators function
fun defaultIndicators(cruiseControl: Boolean): List<ClusterIndicator> {
    return listOf(
        // Other indicators...
        ClusterIndicator(
            drawableRes = if (cruiseControl) 
                R.drawable.ic_cruise_control_active
            else 
                R.drawable.ic_cruise_control_default,
            visible = true
        ),
        // Other indicators...
    )
}
```

## Step 8: Add Control Button for Cruise Control
(optional if need to have user interaction)

If you want a control button, update the `ControlButtons` component:

```kotlin
// In ControlButtons.kt
@Composable
fun ControlButtons(
    modifier: Modifier = Modifier,
    onAction: (ClusterAction) -> Unit,
    state: ClusterState
) {
    // Get cruise control painter
    val cruiseControlPainter = painterResource(
        id = if (state.cruiseControl) 
            R.drawable.ic_cruise_control_active
        else 
            R.drawable.ic_cruise_control_default
    )
    
    Row(/* existing code */) {
        // Existing buttons...
        
        // Cruise Control button
        IconButton(onClick = { onAction(ClusterAction.ToggleCruiseControl) }) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = cruiseControlPainter,
                contentDescription = "Toggle Cruise Control"
            )
        }
        
        // Other buttons...
    }
}
```

## Step 9: Update the ViewModel 
(optional if need to have user interaction)

If you added a control button, handle the action in the ViewModel:

```kotlin
// In ClusterViewModel.kt
private fun processAction(action: ClusterAction) {
    when (action) {
        // Existing actions...
        is ClusterAction.ToggleCruiseControl -> toggleCruiseControl()
        // Other actions...
    }
}

private fun toggleCruiseControl() {
    viewModelScope.launch {
        repository.toggleCruiseControl()
    }
}
```

## Step 10: Update the Main Cluster Component

Pass the cruise control state to the top bar:

```kotlin
// In Cluster.kt
ClusterTopBar(
    modifier = Modifier.width(topBarWidth),
    cruiseControl = state.cruiseControl
)
```

## Step 11: Testing the Feature

1. Run the app on your device or emulator
2. Test with MQTT messages:
    
    ```
    // JSON format
    {"CruiseControl": true}
    
    // Key-value format
    cruisecontrol=true
    ```
For the key-value we are using a mosquitto server in a linux machine. For local testing purposes.
Go to the terminal in a Linux environment and do the following commands.

    sudo apt install -y mosquitto mosquitto-clients // Install the mosquitto server
    sudo systemctl enable --now mosquitto // enable the server

    mosquitto_pub -h 127.0.0.1 -p 1883 -V mqttv5 -t 'd/test/9999/01/dead/device/0001/01/1000' -m 'cruisecontrol=true' // <feature_mqtt_filter>=<value>

    
3. If you added a control button, test toggling cruise control with the button

