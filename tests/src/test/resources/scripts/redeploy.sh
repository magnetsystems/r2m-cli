#!/bin/sh
LOG=redeploy.log
echo "On Server running at ${1}, deploying Application ${2} location at ${3} ..." | tee $LOG 
SETTINGS_OPTIONS=
if [ -e $1 ]; 
then
  SETTINGS_OPTIONS="--s $1"
  shift
  echo "mvn $SETTINGS_OPTIONS com.oracle.weblogic:wls-maven-plugin:redeploy -X -Dverbose=true -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software -Dadminurl=${1} -Dname=${2} -Dsource=${3}"
  mvn $SETTINGS_OPTIONS com.oracle.weblogic:wls-maven-plugin:redeploy -X -Dverbose=true -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software -Dadminurl=${1} -Dname=${2} -Dsource=${3} 2>&1 | tee -a $LOG
else
  echo "mvn com.oracle.weblogic:wls-maven-plugin:redeploy -X -Dverbose=true -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software -Dadminurl=${1} -Dname=${2} -Dsource=${3}"
  mvn com.oracle.weblogic:wls-maven-plugin:redeploy -X -Dverbose=true -Dpassword=welcome1 -Duser=weblogic -DmiddlewareHome=$HOME/Oracle/Software -Dadminurl=${1} -Dname=${2} -Dsource=${3} 2>&1 | tee -a $LOG
fi
