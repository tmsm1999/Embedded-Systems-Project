#!/bin/bash
#
# common.sh
# Shell functions and variable common to all start/stop/status scripts
# This script is not executed directly, but is sourced by other scripts.

# Get the name of the application as the last element of the application path.
# Parameters: $1=Application_directory
get_app_name() {
  if [[ "$1" ]]
  then
    APP_NAME=$( basename "$1" )
    if [[ "$APP_NAME" ]]
    then
      echo "$APP_NAME"
    else
      echo "Error: common->get_app_name() Unable to determine application name"
    fi
  else
    echo "Error: common->get_app_name() requires appdir parameter"
  fi
}

# Get PID of the application process if it is running, otherwise return
# nothing.
# Parameters:  $1=Application_name
get_app_pid() {
  if [[ "$1" ]]
  then
    PID="$( pgrep -f """.*${1}.py""" )"
    if [[ "$PID" ]]
    then
      echo "$PID"
    fi
  else
    echo "Error: common->get_app_pid requires appname parameter"
    exit 1
  fi
}
