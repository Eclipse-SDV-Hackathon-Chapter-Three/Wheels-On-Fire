FROM python:3.12-slim-bookworm as base

RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install \
    git \
    wget \
    unzip \
    android-tools-adb \
    android-tools-fastboot \
    aapt \
    && rm -rf /var/lib/apt/lists/*


# Create app directory
RUN mkdir -p /app /var/log

# Set working directory
WORKDIR /app

# Copy the Python script and requirements
COPY apk_installer.py /app/
COPY requirements.txt /app/

# Install Python dependencies
RUN pip3 install -r requirements.txt

# Copy the provisioning directory with APKs
COPY provisioning/ /app/provisioning/

# Make script executable
RUN chmod +x /app/apk_installer.py

# Environment variables for ADB to connect to host
ENV ADB_SERVER_HOST=host.docker.internal
ENV ADB_SERVER_PORT=5037
ENV ANDROID_HOME=/app

# Default command
ENTRYPOINT ["python3", "/app/apk_installer.py"]
CMD ["--dir", "/app/provisioning", "--uninstall", "--workload-name", "apk_installer"]
