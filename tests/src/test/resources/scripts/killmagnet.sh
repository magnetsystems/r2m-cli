#!/bin/sh
# ----------------------------------------------------------------------
#    Copyright (c) 2013 Magnet Systems, Inc. All rights reserved.
# ----------------------------------------------------------------------
#
# File       : killopus.sh
# Description: Script to kill local opus process(es) given a pattern 
#
#

PATTERN=$1
TMP_FILE=/tmp/opus.pids

usage() {

    echo "Usage: $0 [-h] [PATTERN]"
    echo " -h                        : print this message"
    echo " PATTERN                   : pattern used to filter java process"
    echo "$0: kill Opus process related processes"
    echo

}

killProcesses() {
    echo "Killing Opus processes with pattern $PATTERN..."
    jps -l | grep $PATTERN | grep -v grep > $TMP_FILE
    cat $TMP_FILE | while read pid prog
    do
	kill -9 $pid
        echo "Killed process '$prog' with ID '$pid'"
    done
    rm $TMP_FILE
}

if [ $# -eq 0 ];
then
    PATTERN=OpusMain
    killProcesses
    PATTERN=".*-server.*-.*.jar"
fi
while [ $# -gt 0 ]
do
    case "$1" in
	-h)
	    usage
	    exit 0
	    ;;
    esac
    shift
done

killProcesses
