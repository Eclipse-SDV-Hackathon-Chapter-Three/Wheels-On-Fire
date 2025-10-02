# ---- Base (your original) ----
FROM ghcr.io/eclipse-symphony/symphony-api:0.48-proxy.41 AS base

ENV DEBIAN_FRONTEND=noninteractive
SHELL ["/bin/bash", "-lc"]

# Your artifacts
ADD ./libmy_provider.so /extensions/libmy_provider.so
ADD ./elf/network_raw_mqtt /extensions/network_raw_mqtt
ADD ./infotainment/app-withoutfeature.apk /extensions/app-withoutfeature.apk
RUN chmod +x /extensions/network_raw_mqtt || true

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
      ca-certificates curl usbutils pkgconf build-essential \
      ninja-build libclang-dev cmake libudev-dev \
      gcc-arm-none-eabi binutils-arm-none-eabi \
      libnewlib-arm-none-eabi libnewlib-dev \
      android-tools-adb \
      usbutils \
      udev \
      libstdc++-arm-none-eabi-dev libstdc++-arm-none-eabi-newlib \
    && rm -rf /var/lib/apt/lists/*

# Install probe-rs tools (installs `probe-rs` in /usr/local/bin)
RUN curl --proto '=https' --tlsv1.2 -LsSf \
  https://github.com/probe-rs/probe-rs/releases/latest/download/probe-rs-tools-installer.sh \
  | sh -s

RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs \
      | sh -s -- -y --default-toolchain stable
ENV PATH="/root/.cargo/bin:${PATH}"

RUN curl --proto '=https' --tlsv1.2 -LsSf https://github.com/probe-rs/probe-rs/releases/latest/download/probe-rs-tools-installer.sh | sh

EXPOSE 5037