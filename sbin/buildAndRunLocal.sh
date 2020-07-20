#!/usr/bin/env bash
set -ex

SCRIPT_DIR=$(cd "$(dirname "$0")" ; pwd -P)
ROOT_DIR="${SCRIPT_DIR}/.."

java_8_home=${1:-/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home}
if [ -z "${java_8_home}" ]; then
  echo "java_8_home not set. Usage <JAVA 8 HOME PATH>"
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

#pushd "${ROOT_DIR}/docker" > /dev/null
#  docker-compose.sh --project-directory . -f docker-compose.yml up --build -d
#popd > /dev/null