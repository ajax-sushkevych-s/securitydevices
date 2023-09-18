#!/bin/bash

./gradlew build

docker-compose up secutiry-app --build --force-recreate
