# AprilTag Detection Sample (ROS 2 + Muto + Symphony)

<img src="https://github.com/ros-misc-utilities/apriltag_detector/raw/master/images/apriltags.png" width="40%" alt="AprilTags example" />

Lightweight AprilTag detection and (optional) drawing pipeline deployed remotely via **Eclipse Muto** (stack orchestration) and **Eclipse Symphony** (fleet update orchestration).

This sample uses the upstream [apriltag_detector packages](https://github.com/ros-misc-utilities/apriltag_detector) to detect / visualize fiducials. For combined detection + camera pose estimation use the alternative project: [apriltag_ros](https://github.com/christianrauch/apriltag_ros).

Focus: perception only (no camera calibration or full 6‑DoF pose fusion). Useful when approximate detection + ID stream is sufficient or camera calibration is unknown.

## Artifact Map

Directory: [`samples/april-tag-robot/`](./)

| File | Purpose |
|------|---------|
| `apriltag-detector-muto-stack.json` | Declarative Muto stack (JSON) for detector + draw nodes (composable container) |
| `apriltag-tracking-solution.json` | Symphony Solution (wraps stack as base64 payload) |
| `apriltag-tracking-instance.json` | Symphony Instance binding Solution → Target |
| `apriltag_workspace/stack.json` | Alternative workspace/stack form (if using archive packaging) |
| `apriltag_workspace/launch/apriltag.launch.yaml` | Upstream style launch YAML (reference only) |
| `apriltag_workspace/config/tags_36h11.yaml` | Tag family parameter file |

Target definition is auto‑registered by the running Muto Agent (see talker–listener sample). For a static reference, review [`../talker-listener/test-robot-debug-target.json`](../talker-listener/test-robot-debug-target.json).

## Scenario Overview

You operate a fleet of robots each with a monocular camera. You need to:
1. Deploy an initial AprilTag detection capability (v1).
2. Safely roll out an improved pipeline (v2) that supports additional tag families and optional tracking/visualization.
3. Use canary rollout + rollback policies to limit risk.

The complete deep‑dive OTA narrative (architecture + sequence diagrams) lives in `ros_variant/muto.md` (search for "AprilTag" section). This README keeps a concise, actionable version focused on running the sample.

### Version Delta (Conceptual)
| Aspect | v1 (baseline) | v2 (enhanced) |
|--------|---------------|---------------|
| Families | `tag36h11` | `tag36h11`, `tagStandard41h12` |
| Nodes | detector, draw | detector, draw, (optional future tracker) |
| Telemetry | raw detections | detections + extended metrics |
| Policy | none | optional: only when docked |

Refer to the architecture + sequence diagrams in `muto.md` for full context. Keeping this README lean.

## Deploy the Sample

### 1. Start Symphony + Muto (Target auto‑registers)
Follow the talker–listener quick start: [`../../muto-quickstart.md`](../../muto-quickstart.md). Ensure a target like `test-robot-debug` appears in Symphony (`/v1alpha2/targets`).

### 2. Inspect / Adjust Stack
Open [`apriltag-detector-muto-stack.json`](./apriltag-detector-muto-stack.json). Key areas:
- `composable[0].node[]` list (detector + draw components)
- Detector parameters (family, image topic remaps)

Edit as desired (e.g., add a second detector node for another family) then base64 encode if embedding directly into a Solution.

### 3. Create Solution
Use the provided Solution definition: [`apriltag-tracking-solution.json`](./apriltag-tracking-solution.json).

If you modify the stack JSON, regenerate the `data` field:
```bash
cd ros_variant/samples/april-tag-robot
STACK=apriltag-detector-muto-stack.json
BASE64=$(cat "$STACK" | base64 -w0)
jq --arg d "$BASE64" '.spec.components[0].properties.data=$d' apriltag-tracking-solution.json > /tmp/new-solution.json
mv /tmp/new-solution.json apriltag-tracking-solution.json
```
Then submit:
```bash
cd ../  # ensure you are in samples/ for the script
./define-solution.sh april-tag-robot/apriltag-tracking-solution.json
```

### 4. Deploy Instance
Submit the instance spec: [`apriltag-tracking-instance.json`](./apriltag-tracking-instance.json)
```bash
./define-instance.sh april-tag-robot/apriltag-tracking-instance.json
```

### 5. Verify
ROS side (in container / host with ROS env):
```bash
ros2 topic list | grep apriltag
ros2 topic echo /apriltag/detections  # Ctrl+C after a few messages
```
Symphony side:
```bash
curl -s http://localhost:8082/v1alpha2/instances | jq '.items[] | select(.metadata.name|test("apriltag")) | {name: .metadata.name, phase: .status.phase}'
```

### 6. (Optional) Canary Update Flow
Create an UpdateRequest (adjust labels to match your fleet):
```json
{
   //TODO (TARGET <- SOLUTIONv2 <- INSTANCEv2)
}
```
POST that JSON to `/v1alpha2/solution`.

## Telemetry
Primary topics (adjust based on stack edits):
- `/apriltag/detections` – core detection results
- `/apriltag/images/debug` – optional visualization (if enabled)


## Policy Ideas
- Only update when `battery_state = docked`
- Abort rollout if detector FPS drops >25% vs baseline
- Enforce homogeneous tag family config (validate before promotion)

## Validation Checklist
| Step | Goal |
|------|------|
| Deploy baseline | Detections stream present |
| Introduce v2 (canary) | No regression in FPS / latency |
| Multi-family enabled | Second family tags appear |
| Rollout full fleet | All targets show updated version |
| Fault injection (camera off) | Recovery without crash loop |
| Optional tracker (future) | Events: APPEARED / LOST |



## Extensions
- Add depth camera + pose refinement node
- GPU-accelerated AprilTag library (v3 experiment)
- Replace MQTT bridge with uProtocol native pub/sub
- Semantic fusion: combine QR + AprilTag detections

## Notes
- Always synchronize schema fields with your deployed Symphony/Muto versions
- For real hardware add a camera device plugin / driver node
- Consider signed container images + attestation (UN R155/R156 alignment)

---
This sample stays intentionally minimal—layer in tracking, fusion, policies, or acceleration as incremental updates while preserving a stable baseline.

