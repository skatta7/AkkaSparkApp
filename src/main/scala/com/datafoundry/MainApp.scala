
package com.datafoundry

import java.util.UUID

import akka.actor.{Actor, ActorSystem, Props}
import com.datafoundry.DataProcessorMain.StartProcess
import com.datafoundry.DataSetLoader.LoadDataSet
import com.datafoundry.SafetyReportProcessor.SafetyReport

import scala.xml.NodeSeq
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.xml.XML

object MainApp extends App {
  val system = ActorSystem("DataProcessor")

  val mainActor = system.actorOf(DataProcessorMain.props, "MainActor")
  mainActor ! StartProcess()
}

object DataProcessorMain {
  case class StartProcess()
  def props:Props = Props(new DataProcessorMain)
}
class DataProcessorMain extends Actor{
  override def receive: Receive = {
    case StartProcess() => {
      val dataSetLoader = context.actorOf(DataSetLoader.props, "DataSetLoaderActor")
      val path = ConfigFactory.load().getString("xmlPath")
      println("=====Data Set Path======= {}", path)
      dataSetLoader ! LoadDataSet(path)
    }
  }
}

case class SafetyReportData(reactionmeddrapt:String,
                            medicinalproduct:String,
                            drugdosageform:String,
                            activesubstancename:String,
                            safetyreportid:String)

object DataSetLoader {
  case class LoadDataSet(path:String)
  def props = Props(new DataSetLoader)
}
class DataSetLoader extends Actor {
  val conf: SparkConf = new SparkConf().setAppName("ReportDataStreamer").setMaster("local[2]")
  //val spark = SparkSession.builder.master("local[4]").getOrCreate()
  val ssc = new StreamingContext(conf, Duration(100))

  override def receive: Receive = {
    case LoadDataSet(path) => {
      println("Loading file ===="+path)
      val xml = XML.loadFile(path)
      val safetyreports: NodeSeq = (xml \\ "ichicsr" \ "safetyreport")
      println("reports found...."+safetyreports.length)

      ReportDataReceiver.setActorSystem(context.system)
      val messageReceiver = new ReportDataReceiver()

      safetyreports.foreach(sr => {
        val actorName = "SafetyReportProcessor" + UUID.randomUUID()
        val reportProcessor = context.actorOf(SafetyReportProcessor.props, actorName)
        reportProcessor ! SafetyReport(sr, messageReceiver.messageReceiverActor)
      })

      ReportMaterializer.startStreaming(ssc, messageReceiver)
    }
  }
}

