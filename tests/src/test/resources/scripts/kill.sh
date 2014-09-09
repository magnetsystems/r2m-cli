#!/bin/sh
# ----------------------------------------------------------------------
#    Copyright (c) 2013 Magnet Systems, Inc. All rights reserved.
# ----------------------------------------------------------------------
#
# File       : kill.sh
# Description: Script to kill all unwanted local wls 
#              leftover instances from failed or interrupted builds 
#
#

PATTERN=weblogic.Server
TMP_FILE=/tmp/wls.pids

usage() {

    echo "Usage: $0 [-h]"
    echo " -h                        : print this message"
    echo "$0: kill all WLS related processes"
    echo

}

killProcesses() {
    echo "Killing all WLS processes..."
    jps -l | grep $PATTERN | grep -v grep > $TMP_FILE
    cat $TMP_FILE | while read pid prog
    do
	kill -9 $pid
        echo "Killed process '$prog' with ID '$pid'"
    done
    rm $TMP_FILE
}

while [ $# -gt 0 ]
do
    case "$1" in
	-h)
	    usage
	    exit 0
	    ;;
	*)
	    echo "Unknown command"
	    usage
	    exit -1
	    ;;	
    esac
    shift
done

killProcesses
