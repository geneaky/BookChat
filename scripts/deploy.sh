#!/usr/bin/env bash

PROJECT_ROOT="/home/ec2-user/app"
cd $PROJECT_ROOT

echo "> 실행"
docker kill $(docker ps -q)
docker-compose up -d