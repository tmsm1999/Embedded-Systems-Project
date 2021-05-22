#!/bin/bash
#
# stop.sh
# Stop application if running.

# Application directory where all applications must reside
APP_HOME="/home/pi/CashierServer"

# Import common functions and set common global environment variables like JAVA_HOME
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
  echo "Sending stop signal to ${APP_NAME} (PID: ${PID}) ..."
  /bin/kill -TERM ${PID}

  while [ -n "${PID}" ]; do
    echo "Waiting for ${APP_NAME} to stop ...";
    sleep 1;
    PID=$( get_app_pid ${APP_NAME} )
  done;

  echo "${APP_NAME} stopped."
else
  echo "${APP_NAME} is not running"
fi

exit 0
