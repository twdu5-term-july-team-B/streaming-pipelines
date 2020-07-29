package com.tw.apps

import StationDataTransformation.nycStationStatusJson2DF
import StationDataTransformation.sfStationStatusJson2DF
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.spark.sql.SparkSession

object StationApp {

  def main(args: Array[String]): Unit = {

    val isTest = if (args.length >= 1 && args(0).equals("test")) true else false

    val zookeeperConnectionString = if (args.length <= 1) "zookeeper:2181" else args(1)

    val retryPolicy = new ExponentialBackoffRetry(1000, 3)

    val zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)

    zkClient.start()

    val stationKafkaBrokers = new String(zkClient.getData.forPath("/tw/stationStatus/kafkaBrokers"))

    var nycStationTopic = new String(zkClient.getData.watched.forPath("/tw/stationDataNYC/topic"))
    var sfStationTopic = new String(zkClient.getData.watched.forPath("/tw/stationDataSF/topic"))
    var checkpointLocation = new String(zkClient.getData.watched.forPath("/tw/output/checkpointLocation"))
    var outputLocation = new String(zkClient.getData.watched.forPath("/tw/output/dataLocation"))

    if (isTest) {
      nycStationTopic = new String(zkClient.getData.watched.forPath("/tw/stationDataNYCTest/topic"))
      sfStationTopic = new String(zkClient.getData.watched.forPath("/tw/StationDataTestSF/topic"))
      checkpointLocation = new String(
        zkClient.getData.watched.forPath("/tw/testOutput/checkpointLocation"))
      outputLocation = new String(
        zkClient.getData.watched.forPath("/tw/testOutput/dataLocation"))
    }

    val spark = SparkSession.builder
      .appName("StationConsumer")
      .getOrCreate()

    import spark.implicits._

    val nycStationDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", stationKafkaBrokers)
      .option("subscribe", nycStationTopic)
      .option("startingOffsets", "latest")
      .load()
      .selectExpr("CAST(value AS STRING) as raw_payload")
      .transform(nycStationStatusJson2DF(_, spark))

    val sfStationDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", stationKafkaBrokers)
      .option("subscribe", sfStationTopic)
      .option("startingOffsets", "latest")
      .load()
      .selectExpr("CAST(value AS STRING) as raw_payload")
      .transform(sfStationStatusJson2DF(_, spark))

    nycStationDF
      .union(sfStationDF)
      .as[StationData]
      .groupByKey(r=>r.station_id)
      .reduceGroups((r1,r2)=>if (r1.last_updated > r2.last_updated) r1 else r2)
      .map(_._2)
      .writeStream
      .format("overwriteCSV")
      .outputMode("complete")
      .option("header", true)
      .option("truncate", false)
      .option("checkpointLocation", checkpointLocation)
      .option("path", outputLocation)
      .start()
      .awaitTermination()

  }
}