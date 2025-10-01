
# Talker–Listener Sample

This is the classic minimal ROS 2 example with two nodes: a publisher (talker) and a subscriber (listener). The talker publishes incrementing messages; the listener prints them.
Run manually (outside Symphony/Muto) like this:

```bash
# Let’s first launch the listener in background:

ros2 run demo_nodes_cpp listener &

#Now let’s launch the talker and see the logs of both nodes:

ros2 run demo_nodes_cpp talker

#After running the talker you should see something like the messages below:
[INFO] [talker]: Publishing: "Hello world: 1"
[INFO] [listener]: I heard: [Hello world: 1] 
[INFO] [talker]: Publishing: "Hello world: 2"
[INFO] [listener]: I heard: [Hello world: 2] 
````

## Running with Muto & Symphony

This is the default demo in the main quick start guide. Artifacts:
- Stack & solution definition: [`../../samples/talker-listener`](../../samples/talker-listener/)
- Scripts: [`../../samples/define-solution.sh`](../../samples/define-solution.sh), [`../../samples/define-instance.sh`](../../samples/define-instance.sh)

See also the upstream ROS package docs: [demo_nodes_cpp](https://docs.ros.org/en/iron/p/demo_nodes_cpp/)