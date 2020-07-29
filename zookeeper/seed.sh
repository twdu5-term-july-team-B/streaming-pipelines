#!/bin/sh
echo $zk_command
$zk_command rmr /tw
$zk_command create /tw ''

$zk_command create /tw/stationDataNYC ''
$zk_command create /tw/stationDataNYC/topic station_data_nyc
$zk_command create /tw/stationDataNYC/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataNYC/checkpoints

$zk_command create /tw/stationDataNYCTest ''
$zk_command create /tw/stationDataNYCTest/topic station_mock_ny
$zk_command create /tw/stationDataNYCTest/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataNYCTest/checkpoints

$zk_command create /tw/stationInformation ''
$zk_command create /tw/stationInformation/kafkaBrokers $kafka_server
$zk_command create /tw/stationInformation/topic station_information
$zk_command create /tw/stationInformation/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationInformation/checkpoints
$zk_command create /tw/stationInformation/dataLocation hdfs://$hdfs_server/tw/rawData/stationInformation/data

$zk_command create /tw/stationStatus ''
$zk_command create /tw/stationStatus/kafkaBrokers $kafka_server
$zk_command create /tw/stationStatus/topic station_status
$zk_command create /tw/stationStatus/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationStatus/checkpoints
$zk_command create /tw/stationStatus/dataLocation hdfs://$hdfs_server/tw/rawData/stationStatus/data

$zk_command create /tw/stationDataSF ''
$zk_command create /tw/stationDataSF/kafkaBrokers $kafka_server
$zk_command create /tw/stationDataSF/topic station_data_sf
$zk_command create /tw/stationDataSF/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataSF/checkpoints
$zk_command create /tw/stationDataSF/dataLocation hdfs://$hdfs_server/tw/rawData/stationDataSF/data

$zk_command create /tw/stationDataTestSF ''
$zk_command create /tw/stationDataTestSF/kafkaBrokers $kafka_server
$zk_command create /tw/stationDataTestSF/topic station_mock_sf
$zk_command create /tw/stationDataTestSF/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataTestSF/checkpoints
$zk_command create /tw/stationDataTestSF/dataLocation hdfs://$hdfs_server/tw/rawData/stationDataTestSF/data

$zk_command create /tw/stationDataMarseille ''
$zk_command create /tw/stationDataMarseille/kafkaBrokers $kafka_server
$zk_command create /tw/stationDataMarseille/topic station_data_marseille
$zk_command create /tw/stationDataMarseille/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataMarseille/checkpoints
$zk_command create /tw/stationDataMarseille/dataLocation hdfs://$hdfs_server/tw/rawData/stationDataMarseille/data

$zk_command create /tw/stationDataTestInformation ''
$zk_command create /tw/stationDataTestInformation/kafkaBrokers $kafka_server
$zk_command create /tw/stationDataTestInformation/topic station_mock_information
$zk_command create /tw/stationDataTestInformation/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataTestInformation/checkpoints
$zk_command create /tw/stationDataTestInformation/dataLocation hdfs://$hdfs_server/tw/rawData/stationDataTestInformation/data

$zk_command create /tw/stationDataTestStatus ''
$zk_command create /tw/stationDataTestStatus/kafkaBrokers $kafka_server
$zk_command create /tw/stationDataTestStatus/topic station_mock_status
$zk_command create /tw/stationDataTestStatus/checkpointLocation hdfs://$hdfs_server/tw/rawData/stationDataTestStatus/checkpoints
$zk_command create /tw/stationDataTestStatus/dataLocation hdfs://$hdfs_server/tw/rawData/stationDataTestStatus/data

$zk_command create /tw/testOutput ''
$zk_command create /tw/testOutput/checkpointLocation hdfs://$hdfs_server/tw/stationMartTest/checkpoints
$zk_command create /tw/testOutput/dataLocation hdfs://$hdfs_server/tw/stationMartTest/data

$zk_command create /tw/output ''
$zk_command create /tw/output/checkpointLocation hdfs://$hdfs_server/tw/stationMart/checkpoints
$zk_command create /tw/output/dataLocation hdfs://$hdfs_server/tw/stationMart/data
