# shellcheck disable=SC2164
# shellcheck disable=SC2103
docker-compose up -d database
ls -la
cd web-service/yacontest-helper
source packager.sh
cd ..
docker-compose down -v --remove-orphans
docker images | grep -v postgres | awk '{print $3}' | xargs docker rmi
docker-compose build --no-cache
docker-compose up