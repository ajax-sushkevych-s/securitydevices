#!/bin/bash

./gradlew assemble

docker-compose up secutiry-app --build --force-recreate
