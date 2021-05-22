#!/bin/bash
#
# status.sh
# Report if application is currently running or not.

APP_HOME="/home/pi/CashierServer"

# Import common functions and set common global environment variables
source ${APP_HOME}/scripts/common.sh

# This could include additional custom environment variables
source ${APP_HOME}/scripts/application.env

if [[ -z ${APP_NAME} ]]
then
  echo "Error: $0: APP_NAME variable is not defined in application.env"
  exit 1
fi

# Check if app is currently running
PID=$( get_app_pid ${APP_NAME} )

if [[ ${PID} ]]; then
  echo "${APP_NAME} is running (PID: ${PID})"
else
  echo "${APP_NAME} is not running"
fi

exit 0
