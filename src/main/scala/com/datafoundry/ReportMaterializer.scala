package com.datafoundry

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.StreamingContext

object ReportMaterializer {

  def startStreaming(ssc:StreamingContext, messageReceiver:ReportDataReceiver) = {
    println("streaming started===")
    //val lines = AkkaUtils.createStream[String](ssc, ReportDataReceiver.props, "ReportDataReceiver")
    val messageStream = ssc.receiverStream(messageReceiver)

   messageStream.foreachRDD(rdd => {
     val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
     import spark.implicits._
     rdd.toDF().show()
   })

    ssc.start()
    ssc.awaitTermination()
  }

}

