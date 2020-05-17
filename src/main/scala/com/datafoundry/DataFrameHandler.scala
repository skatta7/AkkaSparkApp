/*
package com.datafoundry

import akka.actor.{Actor, Props}
import com.datafoundry.DataFrameHandler.SafetyReportData
import org.apache.spark.sql.SparkSession

object DataFrameHandler{
  case class SafetyReportData(reactionmeddrapt:String,
                             medicinalproduct:String,
                             drugdosageform:String,
                             activesubstancename:String,
                             safetyreportid:String)

  def props = Props(new DataFrameHandler)
}
class DataFrameHandler extends Actor{

  val spark = SparkSession.builder.master("local[4]").getOrCreate()
  import spark.implicits._
  implicit val formats = org.json4s.DefaultFormats
  var dataFrame = spark.emptyDataset[SafetyReportData].toDF()
  var counter = 0
  spark.conf.set("spark.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation","true")

  override def receive: Receive = {
    case data: SafetyReportData => {
      val df = spark.createDataset(Seq(data)).toDF()
      val selectedDF =  df.select("reactionmeddrapt",
        "medicinalproduct",
        "drugdosageform",
        "activesubstancename",
        "safetyreportid"
      )
      dataFrame = dataFrame.union(selectedDF)
      counter += 1
      if(counter == 500) {
        dataFrame.write.mode("overwrite").saveAsTable("safetyreports")
        spark.sql("select * from safetyreports").show(500)
        spark.close()
      }
    }
  }
}
*/
