#!/usr/bin/env python3

import os
import time
import signal
import logging
import argparse
import sys
import json

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("/var/log/wait_for_signal.log")
    ]
)
logger = logging.getLogger("signal-waiter")

# Global flag to indicate if we should proceed
proceed_flag = False

def signal_handler(sig, frame):
    """Handle signals to wake up the process"""
    global proceed_flag
    if sig == signal.SIGUSR1:
        logger.info("Received SIGUSR1 signal - proceeding with execution")
        proceed_flag = True
    elif sig == signal.SIGTERM:
        logger.info("Received SIGTERM signal - exiting")
        sys.exit(0)

def wait_for_signal_file(signal_file_path, check_interval=5):
    """Wait for a signal file to appear"""
    logger.info(f"Waiting for signal file: {signal_file_path}")
    while not os.path.exists(signal_file_path):
        logger.debug(f"Signal file not found, checking again in {check_interval} seconds")
        time.sleep(check_interval)
    
    logger.info(f"Signal file found: {signal_file_path}")
    
    # Read the signal file content
    try:
        with open(signal_file_path, 'r') as f:
            content = f.read().strip()
        logger.info(f"Signal file content: {content}")
        return content
    except Exception as e:
        logger.error(f"Error reading signal file: {e}")
        return None

def main():
    parser = argparse.ArgumentParser(description="Wait for a signal before proceeding")
    parser.add_argument("--signal-mode", choices=["file", "process"], default="file",
                      help="Mode to wait for signal: file or process signal (default: file)")
    parser.add_argument("--signal-file", default="/tmp/proceed_signal",
                      help="Path to the signal file (default: /tmp/proceed_signal)")
    parser.add_argument("--timeout", type=int, default=0,
                      help="Timeout in seconds (0 for no timeout, default: 0)")
    parser.add_argument("--task", default="echo 'Task executed'",
                      help="Command to execute after receiving the signal (default: echo 'Task executed')")
    args = parser.parse_args()
    
    # Log script start
    logger.info(f"Wait-for-signal started with arguments: {sys.argv[1:]}")
    
    # Register signal handlers if using process signals
    if args.signal_mode == "process":
        signal.signal(signal.SIGUSR1, signal_handler)
        signal.signal(signal.SIGTERM, signal_handler)
        
        logger.info(f"Waiting for SIGUSR1 signal (PID: {os.getpid()})")
        logger.info(f"To send signal: kill -SIGUSR1 {os.getpid()}")
        
        # Wait for the signal or timeout
        start_time = time.time()
        while not proceed_flag:
            time.sleep(1)
            if args.timeout > 0 and time.time() - start_time > args.timeout:
                logger.warning(f"Timeout after {args.timeout} seconds")
                return 1
    else:
        # Wait for the signal file
        start_time = time.time()
        while True:
            if os.path.exists(args.signal_file):
                signal_content = wait_for_signal_file(args.signal_file)
                break
            
            time.sleep(1)
            if args.timeout > 0 and time.time() - start_time > args.timeout:
                logger.warning(f"Timeout after {args.timeout} seconds")
                return 1
    
    # Signal received, execute the task
    logger.info(f"Executing task: {args.task}")
    try:
        exit_code = os.system(args.task)
        logger.info(f"Task completed with exit code: {exit_code}")
        return exit_code >> 8  # Extract the actual exit code
    except Exception as e:
        logger.error(f"Error executing task: {e}")
        return 1

if __name__ == "__main__":
    exit_code = main()
    sys.exit(exit_code)