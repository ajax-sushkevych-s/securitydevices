#!/bin/bash

./gradlew assemble

docker-compose up security-devices-app --build --force-recreate
