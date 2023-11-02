#!/bin/bash

./gradlew assemble

docker-compose up security-devices-app-1 security-devices-app-2 --build --force-recreate
