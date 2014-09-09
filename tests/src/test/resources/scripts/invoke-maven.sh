#!/bin/sh
LOG=invoke-maven.log
echo "Invoking maven with properties $*..." | tee $LOG
SETTINGS_OPTIONS=
if [ -e $1 ]; 
then
  SETTINGS_OPTIONS="--s $1"
  shift
  echo "mvn $SETTINGS_OPTIONS -Dandroid.sdk.path=$ANDROID_HOME $* "
  mvn $SETTINGS_OPTIONS $* 2>&1 | tee -a $LOG
else
  echo "mvn -Dandroid.sdk.path=$ANDROID_HOME $* "
  mvn -Dandroid.sdk.path=$ANDROID_HOME $* 2>&1 | tee -a $LOG
fi
