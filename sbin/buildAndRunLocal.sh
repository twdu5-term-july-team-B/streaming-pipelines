#!/usr/bin/env bash
set -ex

SCRIPT_DIR=$(cd "$(dirname "$0")" ; pwd -P)
ROOT_DIR="${SCRIPT_DIR}/.."

java_8_home=$1
if [ -z "${java_8_home}" ]; then
  echo "java_8_home not set. Usage <JAVA 8 HOME PATH>"
  echo "Hint: run `java -version`. If not running Java 8, switch to Java 8. Your Java 8 Home path can be found by running `echo $JAVA_HOME`"
  exit 1
fi

export JAVA_HOME=$java_8_home

pushd "${ROOT_DIR}/CitibikeApiProducer" > /dev/null
  ./gradlew clean bootJar
popd > /dev/null

pushd "${ROOT_DIR}/RawDataSaver" > /dev/null
  sbt package
popd > /dev/null

pushd "${ROOT_DIR}/StationConsumer" > /dev/null
  sbt package
popd > /dev/null

pushd "${ROOT_DIR}/StationTransformerNYC" > /dev/null
  sbt package
popd > /dev/null

pushd "${ROOT_DIR}/docker" > /dev/null
  docker-compose.sh --project-directory . -f docker-compose.yml up --build -d
popd > /dev/null