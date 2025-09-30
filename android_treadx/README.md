# Digital Cluster MQTT Integration

## Overview
This project implements a digital cluster application for Android that communicates with a vehicle system using MQTT protocol. The application publishes vehicle speed and cruise control commands, while the ThreadX board both subscribes to these commands and publishes cruise control status updates, all using a single MQTT topic.

## Architecture
The system consists of two main components:

1. Android Application (Digital Cluster) - Publishes vehicle speed and cruise control commands to the MQTT broker. Users interact with the interface to control vehicle features, including increasing and decreasing speed.

2. ThreadX Board - Subscribes to vehicle parameters from the Android app and processes them. It has a physical button to deactivate cruise control and publishes the updated cruise control status back to the same topic.


## Communication Flow

```

┌─────────────────┐      MQTT Broker      ┌─────────────────┐
│                 │         Topic:         │                 │
│   Android App   │─────────────────────▶ │ ThreadX Board   │
│   (Digital      │  "vehicle/parameters"  │ (Vehicle        │
│    Cluster)     │                        │  Controller)    │
│                 │                        │                 │
└─────────────────┘                        └────────┬────────┘
                                                    │
                                                    │ Physical button
                                                    │ press deactivates
                                                    │ cruise control
                                                    ▼
┌─────────────────┐      MQTT Broker      ┌─────────────────┐
│                 │         Topic:         │                 │
│   Android App   │◀────────────────────── │ ThreadX Board   │
│   (Digital      │  "vehicle/parameters"  │ (Vehicle        │
│    Cluster)     │                        │  Controller)    │
│                 │                        │                 │
└─────────────────┘                        └─────────────────┘

```

## Data Format

```
{
  "Speed": 75,
  "RPM": 2500,
  "CruiseControl": false,
  "Gear": "D",
  "AmbientTemperature": 22,
  "EngineTemperature": 90,
  "Battery": 95,
  "Range": 350,
  "Economy": "8.5L/100km",
  "TypeOfVehicle": 0,
  "SpeedUnit": "km/h",
  "TemperatureUnit": 0,
  "ShareLocation": true
}

```


## Cruise Control and Speed Control Functionality
The cruise control and speed control features work as follows:

### 1. Speed Control from Android App:

- The Android app publishes the vehicle speed to the ThreadX board via the "vehicle/parameters" topic
- Users can increase or decrease the speed using the app interface, which sends updated speed values
- The ThreadX board adjusts the vehicle speed according to these commands

### 2. Cruise Control Activation from Android App:

- When the user activates cruise control in the Android app, it sends a message with "CruiseControl": true on the same topic
- The ThreadX board receives these parameters and maintains the current speed when cruise control is active

### 3. Cruise Control Status Updates from ThreadX Board:

- The ThreadX board subscribes to the "vehicle/parameters" topic to receive commands
- It also publishes to the same topic to update the cruise control status
- When cruise control state changes (either by activation from app or deactivation from button), the board publishes the updated status

### 4. Deactivation from ThreadX Board:

- The ThreadX board has a physical button that can deactivate cruise control
- When pressed, if cruise control is active, it will deactivate it and publish the updated status to the "vehicle/parameters" topic with "CruiseControl": false
- If cruise control is already inactive, the button press has no effect
- The Android app receives this status update and updates its UI accordingly