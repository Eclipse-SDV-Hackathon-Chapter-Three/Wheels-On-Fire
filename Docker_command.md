# 1. Commands to build and run the docker

## Build
```
docker build -t adb-cli .
```

## Run for USB version
```
docker run --rm -it \
  --privileged \                                   # easiest for USB + SELinux on Fedora
  --device /dev/bus/usb:/dev/bus/usb \             # pass the USB bus
  -v "$PWD/.android:/root/.android" \              # persist adb keys/authorizations
  adb-cli
```

# Run for Emulator version
```
docker run --rm -it \
  --network host \
  -v "$PWD/.android:/root/.android" \
  -e ADB_SERVER_SOCKET=tcp:127.0.0.1:5037 \
  adb-cli devices -l
```