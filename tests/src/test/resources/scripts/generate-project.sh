#!/bin/sh
LOG=generate-project.log
echo "Generating project with configuration file: $*..." | tee $LOG
SETTINGS_OPTIONS=
if [ -e $1 ]; 
then
  SETTINGS_OPTIONS="--s $1"
  shift
  echo "mvn $SETTINGS_OPTIONS com.magnet.tools:magnet-tools-maven-plugin:1.1.7:create  -Dandroid.sdk.path=$ANDROID_HOME  $*"
  mvn $SETTINGS_OPTIONS com.magnet.tools:magnet-tools-maven-plugin:1.1.7:create -Dandroid.sdk.path=$ANDROID_HOME  $* 2>&1 | tee -a $LOG
else
  echo "mvn com.magnet.tools:magnet-tools-maven-plugin:1.1.7:create  -Dandroid.sdk.path=$ANDROID_HOME  $*"
  mvn com.magnet.tools:magnet-tools-maven-plugin:1.1.7:create -Dandroid.sdk.path=$ANDROID_HOME  $* 2>&1 | tee -a $LOG
fi
