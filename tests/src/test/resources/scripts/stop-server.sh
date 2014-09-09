#!/bin/sh
LOG=stop-server.log
SETTINGS_OPTIONS=
if [ -e $1 ]; 
then
  SETTINGS_OPTIONS="--s $1"
  shift
  echo "mvn $SETTINGS_OPTIONS com.oracle.weblogic:wls-maven-plugin:stop-server -DtimeoutSecs=30 -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software  -DdomainHome=${1}"
  mvn $SETTINGS_OPTIONS com.oracle.weblogic:wls-maven-plugin:stop-server -DtimeoutSecs=30 -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software  -DdomainHome=${1} 2>&1 | tee $LOG
else
  echo "mvn com.oracle.weblogic:wls-maven-plugin:stop-server -DtimeoutSecs=30 -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software  -DdomainHome=${1}"
  mvn com.oracle.weblogic:wls-maven-plugin:stop-server -DtimeoutSecs=30 -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software  -DdomainHome=${1} 2>&1 | tee $LOG
fi
