package com.tw.apps

import StationInformationTransformation.stationInformationJson2DF
import StationStatusTransformation._
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.TimestampType

object StationApp {

  def main(args: Array[String]): Unit = {

    var isTest = false

    var zookeeperConnectionString = "zookeeper:2181"

    if (args.length >=1 && args(0).equals("test")){
      isTest = true
      if (args.length >= 2) {
        zookeeperConnectionString = args(1)
      }
    }
    else if (args.length >= 1) {
      zookeeperConnectionString = args(0)
    }

    val retryPolicy = new ExponentialBackoffRetry(1000, 3)

    val zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)

    zkClient.start()

    var kafkaBrokers: String = ""

    var stationStatusTopic: String = ""
    var stationInformationTopic: String = ""
    var stationDataNYC: String = ""
    var checkpointLocation: String = ""

    if(isTest) {
      kafkaBrokers = new String(zkClient.getData.forPath("/tw/StationDataTestStatus/kafkaBrokers"))
      stationStatusTopic = new String(zkClient.getData.watched.forPath("/tw/StationDataTestStatus/topic"))
      stationInformationTopic = new String(zkClient.getData.watched.forPath("/tw/StationDataTestInformation/topic"))
      stationDataNYC = new String(zkClient.getData.watched.forPath("/tw/stationDataNYCTest/topic"))
      checkpointLocation = new String(
        zkClient.getData.watched.forPath("/tw/testOutput/checkpointLocation"))
    }
    else {
      kafkaBrokers = new String(zkClient.getData.forPath("/tw/stationStatus/kafkaBrokers"))
      stationStatusTopic = new String(zkClient.getData.watched.forPath("/tw/stationStatus/topic"))
      stationInformationTopic = new String(zkClient.getData.watched.forPath("/tw/stationInformation/topic"))
      stationDataNYC = new String(zkClient.getData.watched.forPath("/tw/stationDataNYC/topic"))
      checkpointLocation = new String(zkClient.getData.watched.forPath("/tw/output/checkpointLocation"))
    }

    val spark = SparkSession.builder
      .appName("NewYorkStationTransformer")
      .getOrCreate()

    import spark.implicits._

    val stationInformationDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBrokers)
      .option("subscribe", stationInformationTopic)
      .option("startingOffsets", "latest")
      .load()
      .selectExpr("CAST(value AS STRING) as raw_payload")
      .transform(stationInformationJson2DF(_, spark))
      .withColumn("timestamp", $"last_updated" cast TimestampType)
      .drop("last_updated")
      .withWatermark("timestamp", "90 seconds")

    val stationStatusDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBrokers)
      .option("subscribe", stationStatusTopic)
      .option("startingOffsets", "latest")
      .load()
      .selectExpr("CAST(value AS STRING) as raw_payload")
      .transform(stationStatusJson2DF(_, spark))
      .withColumn("timestamp", $"last_updated" cast TimestampType)
      .withWatermark("timestamp", "30 seconds")

    val stationDataDF = stationStatusDF
      .join(stationInformationDF
        .withColumnRenamed("station_id", "i_station_id")
        .withColumnRenamed("timestamp", "i_timestamp")
        , expr(
          """
            |station_id=i_station_id AND
            |timestamp <= i_timestamp + interval 90 seconds  AND
            |timestamp >= i_timestamp
          """.stripMargin),
        "left_outer")
      .filter($"name".isNotNull)
      .as[StationData]
      .groupByKey(r => r.station_id)
      .reduceGroups((r1, r2) => if (r1.timestamp.after(r2.timestamp)) r1 else r2)
      .map(_._2)
      .drop("timestamp")
      .orderBy($"station_id")

    stationDataDF
      .toJSON
      .selectExpr("CAST(value AS STRING)")
      .writeStream
      .format("kafka")
      .outputMode("complete")
      .option("checkpointLocation", checkpointLocation)
      .option("topic", stationDataNYC)
      .option("kafka.bootstrap.servers", kafkaBrokers)
      .start()
      .awaitTermination()
  }
}