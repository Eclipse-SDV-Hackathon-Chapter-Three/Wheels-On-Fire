# Cruise-Control Example App

This folder contains an example application that implements a simulated Cruise-Control component.

The example uses Eclipse uProtocol to periodically publish the current operational status (e.g. current speed, engine temperature) and to expose an API endpoint for setting the target speed.

## Getting Started

The example is implemented in Rust and therefore requires a [Rust toolchain to be installed](https://rustup.rs/) for building.

```bash
cargo build
```

The application supports using either Eclipse Zenoh or MQTT 5 for exchanging messages. The transport can be selected on the command line.

```bash
cargo run
```

will display all available command line options.

In order to enable informational log statements being printed to the console, the `RUST_LOG` environment variable can be used:

```bash
RUST_LOG=INFO cargo run
```

To enable debug logging for the app, use:

```bash
RUST_LOG=INFO,cruise_control_app=DEBUG cargo run
```

This will enable log statements regarding the sending of status messages.

## 🎯 Run

1. Make sure your MQTT broker is running on the shared notebook (`ank get workloads` on the shared notebook) and your notebook has connection to the shared notebook.
2. Change the manifest file commandOptions with the Shared PC IP address for the MQTT broker (MQTT_BROKER_URI=mqtt://[MQTT IP]:1883)
3. Apply the Ankaios manifest to start the demo applications:
```shell
ank apply cruise-control-app.yaml
```
4. Verify the logs:
```shell
ank logs -f cruise-control-app
```
5. Delete the example workloads
```shell
ank apply -d cruise-control-app.yaml
```

## Change and build example code

The applications are a good starting point. You can enhance the existing code or create new workloads.
All applications managed by Eclipse Ankaios must be containerized. If you change a line of code you must rebuild the container image for that app with:

```shell
sudo podman build -f Dockerfile -t cruise-control-app:0.1 .
```

Afterwards you need to replace the public demo container image (e.g. `ghcr.io/eclipse-sdv-hackathon-chapter-three/sdv-lab/cruise-control-app:latest`) with your custom one (e.g. `cruise-control-app:0.1`) in the Ankaios manifest [cruise-control-app.yaml](./cruise-control-app.yaml) for the specific workload. You can use the existing `Dockerfile` for building.

For a final demo and container image, consider uploading to `ghcr.io/eclipse-sdv-hackathon-chapter-three/sdv-lab/cruise-control-app:latest:<team_name>-<version>`, so that someone who want to try out your final setup does not need to build container images. Replace the `team_name` with your hack team's name and append a version (`0.1`). Replace the existing images with your final ones in the Ankaios manifest [cruise-control-app.yaml](./cruise-control-app.yaml).
