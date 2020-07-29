#!/usr/bin/env bash
set -e

cat zookeeper.properties

echo $PATH

PATH=$PATH nohup zookeeper-server-start.sh zookeeper.properties 1> /tmp/zookeeper.log  2>/tmp/zookeeper.error.log &

nohup kafka-server-start.sh kafka.properties 1> /tmp/kafka.log  2>/tmp/kafka.error.log &

start-master.sh -p 7077

start-slave.sh 127.0.0.1:7077

mkdir -p /tmp/spark-events && start-history-server.sh

mkdir -p /tmp/tw/rawData/stationInformation/checkpoints
mkdir -p /tmp/tw/rawData/stationInformation/data
mkdir -p /tmp/tw/rawData/stationStatus/checkpoints
mkdir -p /tmp/tw/rawData/stationStatus/data
mkdir -p /tmp/tw/rawData/stationSanFrancisco/checkpoints
mkdir -p /tmp/tw/rawData/stationSanFrancisco/data
mkdir -p /tmp/tw/rawData/stationMarseille/checkpoints
mkdir -p /tmp/tw/rawData/stationMarseille/data
mkdir -p /tmp/tw/rawData/stationTest/checkpoints
mkdir -p /tmp/tw/rawData/stationTest/data
mkdir -p /tmp/tw/stationMart/checkpoints
mkdir -p /tmp/tw/stationMart/data
mkdir -p /tmp/tw/stationMartTest/checkpoints
mkdir -p /tmp/tw/stationMartTest/data



zk_command="zookeeper-shell.sh 127.0.0.1:2181"

$zk_command create /tw ''
$zk_command create /tw/stationInformation ''
$zk_command create /tw/stationInformation/kafkaBrokers 127.0.0.1:9092
$zk_command create /tw/stationInformation/topic station_information
$zk_command create /tw/stationInformation/checkpointLocation /tmp/tw/rawData/stationInformation/checkpoints
$zk_command create /tw/stationInformation/dataLocation /tmp/tw/rawData/stationInformation/data

$zk_command create /tw/stationStatus ''
$zk_command create /tw/stationStatus/kafkaBrokers 127.0.0.1:9092
$zk_command create /tw/stationStatus/topic station_status
$zk_command create /tw/stationStatus/checkpointLocation /tmp/tw/rawData/stationStatus/checkpoints
$zk_command create /tw/stationStatus/dataLocation /tmp/tw/rawData/stationStatus/data

$zk_command create /tw/stationDataSF ''
$zk_command create /tw/stationDataSF/kafkaBrokers 127.0.0.1:9092
$zk_command create /tw/stationDataSF/topic station_data_sf
$zk_command create /tw/stationDataSF/checkpointLocation /tmp/tw/rawData/stationDataSF/checkpoints
$zk_command create /tw/stationDataSF/dataLocation /tmp/tw/rawData/stationDataSF/data

$zk_command create /tw/stationDataMarseille ''
$zk_command create /tw/stationDataMarseille/kafkaBrokers 127.0.0.1:9092
$zk_command create /tw/stationDataMarseille/topic station_data_marseille
$zk_command create /tw/stationDataMarseille/checkpointLocation /tmp/tw/rawData/stationDataMarseille/checkpoints
$zk_command create /tw/stationDataMarseille/dataLocation /tmp/tw/rawData/stationDataMarseille/data

$zk_command create /tw/StationDataTest ''
$zk_command create /tw/StationDataTest/kafkaBrokers 127.0.0.1:9092
$zk_command create /tw/StationDataTest/topic/info station_mock_information
$zk_command create /tw/StationDataTest/topic/status station_mock_status
$zk_command create /tw/StationDataTest/topic/sf station_mock_sf
$zk_command create /tw/StationDataTest/checkpointLocation /tmp/tw/rawData/StationDataTest/checkpoints
$zk_command create /tw/StationDataTest/dataLocation /tmp/tw/rawData/StationDataTest/data

$zk_command create /tw/testOutput ''
$zk_command create /tw/testOutput/checkpointLocation /tmp/tw/stationMartTest/checkpoints
$zk_command create /tw/testOutput/dataLocation /tmp/tw/stationMartTest/data

$zk_command create /tw/output ''
$zk_command create /tw/output/checkpointLocation /tmp/tw/stationMart/checkpoints
$zk_command create /tw/output/dataLocation /tmp/tw/stationMart/data


tail -f /dev/null
