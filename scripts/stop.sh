#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
# current absolute path in which stop.sh is included

source ${ABSDIR}/profile.sh
# this is similar to import in java
# I can use the functions of profile.sh here

IDLE_PORT=$(find_idle_port)
echo "> Check the pid of the applications running on $IDLE_PORT"
IDLE_PID=$(lsof t i tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
	echo "> there is no application running now"
else
	echo "> kill -15 $IDLE_PID"
	kill -15 ${IDLE_PID}
	sleep 5
fi