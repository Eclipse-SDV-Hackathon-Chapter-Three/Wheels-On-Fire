# OTA Updates for Robotics (ROS Variant)

## Documentation Structure

This documentation has been restructured into focused guides:

- **[🚀 Quick Start with Eclipse Muto and Eclipse Symphony](https://github.com/eclipse-muto/muto/blob/main/docs/user_guide/quick_start.md)** - Step-by-step deployment guide with practical examples
- **[📚 Eclipse Muto Overview](https://github.com/eclipse-muto/muto/blob/main/docs/project_overview.md)** - Comprehensive technical architecture and development reference

## Example Scenarios

Two sample scenarios show how Eclipse Muto and Symphony work together to manage ROS 2 software on a robot:

1. **Talker–Listener (Hello ROS Stack)** – Classic minimal ROS 2 demo (publisher + subscriber). Used in the quick start to illustrate the Target → Solution → Instance workflow. See: [`samples/talker-listener`](https://github.com/eclipse-muto/muto/tree/main/docs/samples/talker-listener).
To run this example with a simple JSON and then an rchived workspace, you can follow the instructions in the [examples](https://github.com/eclipse-muto/muto/blob/main/docs/user_guide/running_examples.md)
2. **AprilTag Detection & Tracking** – A perception-oriented example demonstrating composable nodes, parameterized detection, and an OTA updatable multi-version stack. See: [`samples/april-tag-robot`](./samples/april-tag-robot/).

Each scenario provides:
- A Muto stack model (declarative launch equivalent)
- Symphony artifacts (Solution with base64 archive, Instance binding)
- Update path (e.g., baseline → enhanced version)

## Introduction

**Eclipse Muto** is an open-source, declarative orchestrator for managing ROS (Robot Operating System) software stacks on edge devices. It ensures the on-robot runtime matches a desired model—enabling resilient, self-healing, remotely upgradable deployments for fleets of robots and vehicles. Muto’s architecture centers on two cooperating on-device roles:

```
Cloud Backend (any orchestrator)
        |
     [Agent]  <---  On-vehicle Plugins: Protocol (MQTT, HTTP, Zenoh, uProtocol), Digital Twin (Ditto), Orchestration (Symphony)
        |
     [Composer] <--- On-vehicle, manages ROS stack lifecycle
        |
   ROS Nodes / Software Stack
```

- **Agent**: Secure gateway, protocol-agnostic, delivers the model.
- **Composer**: Enforces the model, manages pipelines, builds, and launches.

### Core Orchestration Features
- **Declarative Stack Models** – Complete ROS launch intent serialized and remotely distributable
- **Continuous Reconciliation** – Automatic convergence back to desired state after drift/failure
- **Versioned Updates & Rollback** – Promote or revert stack revisions safely
- **Cloud Integration** – Symphony provider for fleet-scale targeting & rollout strategies


For deep architectural detail see the upstream [Muto documentation](https://github.com/eclipse-muto/muto/blob/main/docs/readme.md).
