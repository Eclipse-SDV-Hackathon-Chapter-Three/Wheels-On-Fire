## Overview

**Digital Cluster App** is a modern automotive instrument cluster simulation built with Android and Jetpack Compose. It provides a customizable digital dashboard that can display vehicle information such as speed, RPM, battery usage , gear position and  power usage  level indicator. The app uses MQTT for real-time communication, making it suitable for integration with vehicle data systems or simulators like CARLA.

## Features

- **Real-time Vehicle Data Display**: Speed, battery usage, gear position and power usage
- **Customizable Driving Modes**: Switch between different driving modes (Race, Sport+, City)
- **Status Indicators**: Cruise control, battery usage, lights, and warning indicators // Only cruise control functionality is working, the rest is only for UI purposes
- **Interactive Controls**: Increase/decrease speed, toggle cruise control, and activate sensors // activate sensors only UI purposes
- **MQTT Communication**: Real-time data exchange using MQTT with local Mosquitto server // currently supporting the two options
- **Vehicle Type Support**: Displays appropriate gauges for electric vehicles

## Architecture

The app follows a modular clean architecture approach:

- **UI Layer**: Jetpack Compose UI components with a ViewModel
- **Domain Layer**: Business logic and models
- **Data Layer**: MQTT communication and repositories
- **Core Modules**: Design system, shared utilities, and common components

### Architecture Diagram

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│  Presentation   │     │     Domain      │     │      Data       │
│                 │     │                 │     │                 │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Compose UI     │     │  Domain Models  │     │  Repositories   │
│  ViewModels     │◄────┤  Use Cases      │◄────┤  Data Sources   │
│  UI Components  │     │  Actions        │     │  MQTT Client    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

## Technology Stack

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for building native UI
- **Hilt**: Dependency injection
- **Coroutines & Flow**: Asynchronous programming and reactive streams
- **MQTT**: Message communication protocol

## Project Structure

```
├── app/                          # Main application module
│   └── DigitalClusterApplication.kt  # Application class with resource preloading
├── core/
│   ├── data/                     # Data layer with repositories and MQTT
│   │   ├── di/                   # Dependency injection modules
│   │   ├── remote/                 # MQTT communication components
│   │   └── repository/           # Data repositories
│   ├── designsystem/             # UI design system and theming
│   └── domain/                   # Business logic and models
│       ├── action/               # User actions and events
│       └── model/                # Domain models
└── feature/
    └── cluster/                  # Cluster display feature
        ├── ui/                   # UI components
        │   ├── component/        # Reusable UI components
        │   └── screen/           # Full screens
        └── viewmodel/            # ViewModels for the feature
```

## Key Components

### Core Components

1. **DigitalClusterApplication**: Main application class that initializes the app and preloads critical resources.
2. **MainActivity**: Entry point that sets up the UI and manages MQTT connections.
3. **MqttModule**: Provides MQTT-related dependencies through Hilt.

### Data Layer

 **MqttClusterBinder**:
1. Connects MQTT messages to the ClusterState.
2. Publishes messages to MQTT topics.
3. Subscribes to MQTT topics and processes incoming messages.

**MqttClusterRepository**: Interface for cluster data operations.
**MqttClusterRepositoryImpl**: Implementation of the repository that handles data operations.

### Domain Layer

1. **ClusterState**: Data class representing the state of the vehicle's instrument cluster.
2. **ClusterAction**: Sealed class representing user actions on the cluster.
3. **CentralScreenState**: Enum defining different central display modes.

### UI Components

1. **Cluster**: Main composable that displays the entire instrument cluster.
2. **ClusterScreen**: Screen that hosts the Cluster composable and connects it to the ViewModel.
3. **ClusterSpeedDisplay**: Displays the vehicle speed with a gauge.
4. **ClusterBatteryDisplay**: Displays battery information for electric vehicles.
5. **ClusterMiddleDisplay**: Displays the central screen content based on the current state.
6. **ClusterTopBar**: Displays status indicators in the top bar.
7. **ControlButtons**: Interactive buttons for controlling the cluster.

### ViewModel

**ClusterViewModel**: Manages the state of the cluster and handles business logic.

## Communication Flow

1. **Data Reception**:
    
    - External data is received via MQTT
    -  `MqttClusterBinder` process incoming messages
    - Data is converted to `ClusterState` objects
2. **State Management**:
    
    - `MqttClusterRepository` maintains the current state
    - `ClusterViewModel` exposes this state to the UI
    - State changes trigger UI updates through Compose's state management
3. **User Interaction**:
    
    - User actions are captured as `ClusterAction` objects
    - `ClusterViewModel` processes these actions
    - Actions may result in state updates or MQTT messages being published
4. **Data Publication**:
    
    - `MqttClusterBinder` sends state updates to MQTT topics
    - External systems can receive and respond to these updates

## Usage

### Control Board

- **Speed Control**: Use the + and - buttons to increase or decrease speed
- **Cruise Control**: Toggle cruise control with the cruise control button

### MQTT Topics

- **vehicle/parameters**: Main topic for vehicle data exchange
- Supports both JSON format and key-value pair format for backward compatibility

### Data Formats

1. **JSON Format** (Primary):

```json
{
  "Speed": 100,
  "CruiseControl": true,
  "RPM": 3500,
  "EngineTemperature": 90,
  "Gear": "D",
  "AmbientTemperature": 25,
  "Economy": "11.6 km/L",
  "SpeedUnit": "mph",
  "Battery": 80,
  "Range": 350,
  "TemperatureUnit": 0,
  "ShareLocation": true,
  "TypeOfVehicle": 0
}
```

2. **Key-Value Format** (Mosquitto):

```
speed=100
cruisecontrol=true
rpm=3500
enginetemp=90
gear=D
```

## Performance Optimizations

1. **Resource Preloading**: Critical drawable resources are preloaded at startup.
2. **Message Batching**: MQTT messages are processed in batches to reduce UI updates.
3. **State Caching**: The ViewModel caches state to avoid unnecessary repository calls.
4. **Composition Optimization**: UI components use `remember` and `derivedStateOf` to prevent unnecessary recompositions.
5. **Debouncing**: User actions are debounced to prevent rapid state changes.