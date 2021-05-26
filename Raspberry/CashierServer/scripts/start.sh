#!/bin/bash
#
# start.sh
# Startup script
#

APP_HOME="/home/pi/CashierServer"

# This could include additional custom environment variables
source ${APP_HOME}/scripts/application.env

source ${APP_HOME}/scripts/common.sh

echo "APP_HOME=${APP_HOME}"
echo "APP_NAME=${APP_NAME}"

# Check if app is currently running
PID=$( get_app_pid ${APP_NAME} )

if [[ ${PID} ]]; then
  # App is running, don't do anything
  echo "${APP_NAME} is already running (PID: ${PID})"
  exit 0
fi

# App is not running, start the app.
echo "Starting ${APP_NAME}"

# Change directory to application home so we know what our current directory is.
cd ${APP_HOME}

# starting server
echo "starting server on http://192.168.192.1:3080/"
nohup python ${APP_HOME}/CashierServer.py > /dev/null 2>&1 &

# Check if app is running
PID=$( get_app_pid ${APP_NAME} )

if [[ ${PID} ]]; then
  echo "${APP_NAME} is running (PID: ${PID})"
  exit 0
else
  echo "${APP_NAME} is NOT running!"
  exit 1
fi
