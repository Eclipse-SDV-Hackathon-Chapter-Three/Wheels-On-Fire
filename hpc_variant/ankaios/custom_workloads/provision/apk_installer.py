#!/usr/bin/env python3

import os
import subprocess
import glob
import logging
import argparse
import sys
import time
import json

# Import Ankaios SDK
try:
    from ankaios_sdk import Ankaios, WorkloadStateEnum, AnkaiosException
except ImportError:
    # Mock implementation for development/testing without SDK
    class WorkloadStateEnum:
        RUNNING = "RUNNING"
        FAILED = "FAILED"
        PENDING = "PENDING"
        SUCCEEDED = "SUCCEEDED"
    
    class AnkaiosException(Exception):
        pass
    
    class Ankaios:
        def __enter__(self):
            return self
        
        def __exit__(self, exc_type, exc_val, exc_tb):
            pass
        
        def get_state(self, field_masks=None):
            return MockState()
        
        def apply_workload(self, workload):
            return MockResponse(["mock-instance-1"])
        
        def wait_for_workload_to_reach_state(self, instance_name, state, timeout):
            time.sleep(2)  # Simulate waiting
            return True
            
        def update_workload_state(self, workload_name, state):
            print(f"Setting workload {workload_name} state to {state}")
            return True
    
    class MockState:
        def get_workload(self, name):
            return MockWorkload()
    
    class MockWorkload:
        def update_agent_name(self, agent_name):
            pass
    
    class MockResponse:
        def __init__(self, added_workloads):
            self.added_workloads = added_workloads

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("/var/log/apk_installer.log")
    ]
)
logger = logging.getLogger("apk-installer")

def check_adb_available():
    """Check if ADB is available in the system."""
    try:
        subprocess.run(["adb", "version"], check=True, capture_output=True, text=True)
        return True
    except (subprocess.SubprocessError, FileNotFoundError):
        logger.error("ADB command not found. Please ensure Android SDK platform-tools are installed and in your PATH.")
        return False

def wait_for_adb_server(timeout=30):
    """Wait for ADB server to be available."""
    start_time = time.time()
    
    # Log ADB server host and port for debugging
    adb_host = os.environ.get('ADB_SERVER_HOST', '127.0.0.1')
    adb_port = os.environ.get('ADB_SERVER_PORT', '5037')
    logger.info(f"Connecting to ADB server at {adb_host}:{adb_port}")
    
    # Set ADB_SERVER_SOCKET environment variable
    os.environ['ANDROID_ADB_SERVER_HOST'] = adb_host
    os.environ['ANDROID_ADB_SERVER_PORT'] = adb_port
    
    while time.time() - start_time < timeout:
        try:
            # Try to connect to the ADB server
            result = subprocess.run(
                ["adb", "devices"], 
                check=False, 
                capture_output=True, 
                text=True,
                timeout=5  # Add timeout to prevent hanging
            )
            
            logger.info(f"ADB devices output: {result.stdout}")
            logger.info(f"ADB devices error: {result.stderr}")
            
            if "List of devices" in result.stdout:
                logger.info("Successfully connected to ADB server")
                return True
                
        except subprocess.TimeoutExpired:
            logger.warning("ADB command timed out")
        except Exception as e:
            logger.warning(f"Error connecting to ADB server: {e}")
        
        logger.info("Waiting for ADB server...")
        time.sleep(2)
    
    logger.error(f"Timed out waiting for ADB server at {adb_host}:{adb_port}")
    return False

def get_connected_devices():
    """Get list of connected Android devices."""
    try:
        result = subprocess.run(["adb", "devices"], check=True, capture_output=True, text=True)
        lines = result.stdout.strip().split('\n')[1:]  # Skip the first line (header)
        devices = []
        
        for line in lines:
            if line.strip() and "\tdevice" in line:
                devices.append(line.split('\t')[0])
        
        return devices
    except subprocess.SubprocessError as e:
        logger.error(f"Error getting device list: {e}")
        return []

def wait_for_device(timeout=60, device_id=None):
    """Wait for an Android device to be connected."""
    start_time = time.time()
    while time.time() - start_time < timeout:
        devices = get_connected_devices()
        
        if devices:
            if device_id:
                if device_id in devices:
                    logger.info(f"Device {device_id} is connected")
                    return device_id
            else:
                logger.info(f"Device {devices[0]} is connected")
                return devices[0]
        
        logger.info("Waiting for device...")
        time.sleep(2)
    
    logger.error("Timed out waiting for device")
    return None

def find_apk_files(directory):
    """Find all APK files in the specified directory."""
    if not os.path.isdir(directory):
        logger.error(f"Directory not found: {directory}")
        return []
    
    apk_files = glob.glob(os.path.join(directory, "*.apk"))
    logger.info(f"Found {len(apk_files)} APK file(s) in {directory}")
    return apk_files

def install_apk(apk_path, device_id=None):
    """Install APK on connected device."""
    if not os.path.isfile(apk_path):
        logger.error(f"APK file not found: {apk_path}")
        return False, "APK file not found"
    
    try:
        cmd = ["adb"]
        if device_id:
            cmd.extend(["-s", device_id])
        
        cmd.extend(["install", "-r", "-d", apk_path])
        
        logger.info(f"Installing APK: {apk_path}")
        result = subprocess.run(cmd, check=False, capture_output=True, text=True)
        
        if result.returncode == 0 and "Success" in result.stdout:
            logger.info(f"Successfully installed {apk_path}")
            return True, "Installation successful"
        else:
            error_msg = result.stderr if result.stderr else result.stdout
            logger.error(f"Installation failed: {error_msg}")
            return False, f"Installation failed: {error_msg}"
            
    except subprocess.SubprocessError as e:
        logger.error(f"Error installing APK: {e}")
        return False, f"Error installing APK: {str(e)}"

def uninstall_package(package_name, device_id=None):
    """Uninstall package from device if it exists."""
    if not package_name:
        return False, "No package name provided"
    
    try:
        cmd = ["adb"]
        if device_id:
            cmd.extend(["-s", device_id])
        
        cmd.extend(["uninstall", package_name])
        
        logger.info(f"Uninstalling package: {package_name}")
        result = subprocess.run(cmd, capture_output=True, text=True)
        
        if result.returncode == 0:
            logger.info(f"Successfully uninstalled {package_name}")
            return True, "Uninstall successful"
        else:
            logger.warning(f"Failed to uninstall {package_name}: {result.stderr}")
            return False, f"Uninstall failed: {result.stderr}"
    except subprocess.SubprocessError as e:
        logger.error(f"Error uninstalling package: {e}")
        return False, f"Error uninstalling package: {str(e)}"

def get_package_name(apk_path):
    """Get package name from APK file using aapt."""
    try:
        result = subprocess.run(
            ["aapt", "dump", "badging", apk_path], 
            check=True, 
            capture_output=True, 
            text=True
        )
        
        for line in result.stdout.split('\n'):
            if line.startswith('package:'):
                for item in line.split(' '):
                    if item.startswith("name='"):
                        return item.split("'")[1]
        
        return None
    except (subprocess.SubprocessError, FileNotFoundError):
        logger.warning(f"Could not determine package name for {apk_path}")
        return None

def create_result_file(results, output_path="/var/log/installation_results.json"):
    """Create a JSON file with installation results."""
    try:
        with open(output_path, 'w') as f:
            json.dump(results, f, indent=2)
        logger.info(f"Results written to {output_path}")
        return True
    except Exception as e:
        logger.error(f"Error writing results: {e}")
        return False

def update_workload_state(workload_name, state):
    """Update the state of this workload in Ankaios."""
    try:
        with Ankaios() as ankaios:
            ankaios.update_workload_state(workload_name, state)
            logger.info(f"Updated workload {workload_name} state to {state}")
            return True
    except Exception as e:
        logger.error(f"Failed to update workload state: {e}")
        return False

def main():
    parser = argparse.ArgumentParser(description="Install APK files from a directory using ADB")
    parser.add_argument("--dir", "-d", default="/app/provisioning", help="Directory containing APK files (default: /app/provisioning)")
    parser.add_argument("--device", "-s", help="Specific device ID to target (optional)")
    parser.add_argument("--wait", "-w", type=int, default=60, help="Wait time in seconds for device (default: 60)")
    parser.add_argument("--uninstall", "-u", action="store_true", help="Uninstall package before installation if possible")
    parser.add_argument("--output", "-o", default="/var/log/installation_results.json", help="Output file for results (default: /var/log/installation_results.json)")
    parser.add_argument("--workload-name", default="apk_installer", help="Name of this workload in Ankaios (default: apk_installer)")
    args = parser.parse_args()
    
    # Log script start
    logger.info(f"APK Installer started with arguments: {sys.argv[1:]}")
    logger.info(f"Looking for APKs in: {args.dir}")
    
    # Check if ADB is available
    if not check_adb_available():
        update_workload_state(args.workload_name, WorkloadStateEnum.FAILED)
        return 1
    
    # Wait for ADB server
    if not wait_for_adb_server():
        update_workload_state(args.workload_name, WorkloadStateEnum.FAILED)
        return 1
    
    # Wait for device
    device_id = wait_for_device(timeout=args.wait, device_id=args.device)
    if not device_id:
        update_workload_state(args.workload_name, WorkloadStateEnum.FAILED)
        return 1
    
    # Find APK files
    apk_files = find_apk_files(args.dir)
    if not apk_files:
        logger.error(f"No APK files found in directory: {args.dir}")
        update_workload_state(args.workload_name, WorkloadStateEnum.FAILED)
        return 1
    
    # Install each APK
    results = {
        "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
        "device": device_id,
        "total_apks": len(apk_files),
        "installations": []
    }
    
    success_count = 0
    for apk_path in apk_files:
        installation_result = {
            "apk": os.path.basename(apk_path),
            "path": apk_path,
            "uninstall_result": None,
            "install_result": None
        }
        
        # Uninstall if requested
        if args.uninstall:
            package_name = get_package_name(apk_path)
            if package_name:
                uninstall_success, uninstall_message = uninstall_package(package_name, device_id)
                installation_result["package_name"] = package_name
                installation_result["uninstall_result"] = {
                    "success": uninstall_success,
                    "message": uninstall_message
                }
        
        # Install the APK
        install_success, install_message = install_apk(apk_path, device_id)
        installation_result["install_result"] = {
            "success": install_success,
            "message": install_message
        }
        
        if install_success:
            success_count += 1
        
        results["installations"].append(installation_result)
    
    # Update summary
    results["success_count"] = success_count
    results["failure_count"] = len(apk_files) - success_count
    
    # Create result file
    create_result_file(results, args.output)
    
    logger.info(f"Installation complete. Successfully installed {success_count} out of {len(apk_files)} APKs.")
    
    # Update workload state based on installation results
    if success_count == len(apk_files):
        update_workload_state(args.workload_name, WorkloadStateEnum.SUCCEEDED)
        return 0
    elif success_count > 0:
        # Partial success - still mark as succeeded but log the partial status
        logger.warning(f"Partial success: {success_count}/{len(apk_files)} APKs installed")
        update_workload_state(args.workload_name, WorkloadStateEnum.SUCCEEDED)
        return 0
    else:
        # Complete failure
        update_workload_state(args.workload_name, WorkloadStateEnum.FAILED)
        return 1

if __name__ == "__main__":
    exit_code = main()
    sys.exit(exit_code)