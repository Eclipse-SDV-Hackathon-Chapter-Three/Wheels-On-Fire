# 1. Commands to build and run the docker

## Build
```
docker build -t adb-cli .
```

## Run
```
docker run --rm -it \
  --privileged \                                   # easiest for USB + SELinux on Fedora
  --device /dev/bus/usb:/dev/bus/usb \             # pass the USB bus
  -v "$PWD/.android:/root/.android" \              # persist adb keys/authorizations
  adb-cli
```