#!/bin/bash

REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=mouken

echo "> Copy Build files"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> check PID of application now running"

CURRENT_PID=$(lsof -ti tcp:8080)
# CURRENT_PID=$(pgrep -fl mouken | grep jar | awk '{print $1}')

echo "> pid : $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "there is no apps running:"
else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 5
fi

echo "> deploy new app"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> authorization to $JAR_NAME"

chmod +x $JAR_NAME

echo "> Run $JAR_NAME"

nohup java -jar \
  -Dspring.config.location=classpath:/application.properties,classpath:/application-aws.properties,/home/ec2-user/app/application-real-db.properties,/home/ec2-user/app/application-mail.properties \
  -Dspring.profiles.active=aws \
  $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &