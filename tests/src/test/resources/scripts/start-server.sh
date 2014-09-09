#!/bin/sh
LOG=start-server.log
echo "Starting Weblogic Server $*..." | tee $LOG
DIR=`dirname $0`
echo "mvn -X -f $DIR/wls-pom.xml -Pstart initialize -DtimeoutSecs=90 $*"
mvn -X -f $DIR/wls-pom.xml -Pstart initialize -DtimeoutSecs=90 $* 2>&1 | tee -a $LOG
