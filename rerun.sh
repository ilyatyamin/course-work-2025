#!/bin/bash
# shellcheck disable=SC2164
cd web-service/yacontest-helper
mvn clean package
# shellcheck disable=SC2103
cd ..
docker-compose build --no-cache
docker-compose up -d database
docker-compose down -v --remove-orphans --volumes --rmi local -f database
docker images | grep -v postgres | awk '{print $3}' | xargs docker rmi -f
docker-compose up --build -d planner_tgbot
docker-compose up -d