#!/bin/bash

# Log file for output
LOG_FILE="/tmp/ankaios_script_$(date +%Y%m%d_%H%M%S).log"

# Function to log messages
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Log start of execution
log "Script started with arguments: $@"

# Example: Parse arguments
PARAM1=""
PARAM2=""

while [[ $# -gt 0 ]]; do
  key="$1"
  case $key in
    --param1)
      PARAM1="$2"
      shift
      shift
      ;;
    --param2)
      PARAM2="$2"
      shift
      shift
      ;;
    *)
      log "Unknown parameter: $1"
      shift
      ;;
  esac
done

# Log parameters
log "Parameter 1: $PARAM1"
log "Parameter 2: $PARAM2"

# Example: Perform some operations
log "Performing operations..."
sleep 2  # Simulate work

# Example: Create a result file
RESULT_FILE="/tmp/script_result_$(date +%Y%m%d_%H%M%S).txt"
echo "Script executed successfully at $(date)" > "$RESULT_FILE"
echo "Param1: $PARAM1" >> "$RESULT_FILE"
echo "Param2: $PARAM2" >> "$RESULT_FILE"

# Log completion
log "Script completed successfully"
log "Results written to: $RESULT_FILE"

# Output the result file path (can be captured by Ankaios)
echo "$RESULT_FILE"

# Exit with success
exit 0
EOF