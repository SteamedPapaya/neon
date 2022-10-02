#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/app/step3
PROJECT_NAME=mouken

echo "> Copy Build files"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> deploy new app"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"
echo "> authorize to $JAR_NAME"
chmod +x $JAR_NAME

echo "> Run $JAR_NAME with profile=$IDLE_PROFILE"
IDLE_PROFILE=$(find_idle_profile)
nohup java -jar \
  -Dspring.config.location=classpath:/application.properties,classpath:/application-aws.properties,/home/ec2-user/app/application-real-db.properties,/home/ec2-user/app/application-mail.properties \
  -Dspring.profiles.active=$IDLE_PROFILE \
  $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &