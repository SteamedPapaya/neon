#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)
IDLE_PROFILE=$(find_idle_profile)

echo "> Health checking"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> IDLE_PROFILE: $IDLE_PROFILE"
echo "> curl -s http://localhost:$IDLE_PORT/app-profile"
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://localhost:${IDLE_PORT}/app-profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then
    echo "> Health check success"
    switch_proxy
    break
  else
    echo "> You can not find the response about health check or It's not running."
    echo "> health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check failure"
    echo "> It doesn't connect to Nginx and the deployment end"
    exit 1
  fi

  echo "> Health check connection failure. retrying..."
  sleep 10
done