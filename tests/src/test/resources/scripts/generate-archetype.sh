#!/bin/sh
LOG=generate-archetype.log
echo "Generating archetype with properties $*..." | tee $LOG
SETTINGS_OPTIONS=
if [ -e $1 ]; 
then
  SETTINGS_OPTIONS="--s $1"
  shift
  echo "mvn $SETTINGS_OPTIONS archetype:generate $* "
  mvn $SETTINGS_OPTIONS archetype:generate -Dandroid.sdk.path=$ANDROID_HOME $* 2>&1 | tee -a $LOG
else
  echo "mvn archetype:generate $* "
  mvn archetype:generate -Dandroid.sdk.path=$ANDROID_HOME $* 2>&1 | tee -a $LOG
fi
