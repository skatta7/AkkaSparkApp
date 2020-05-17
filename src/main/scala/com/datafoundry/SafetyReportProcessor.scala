
package com.datafoundry

import akka.actor.{Actor, ActorRef, Props}
import com.datafoundry.SafetyReportProcessor.SafetyReport
import scala.xml.Node

object SafetyReportProcessor{
  case class SafetyReport(xml:Node, ReportDataReceiver: ActorRef)
  def props = Props(new SafetyReportProcessor)
}
class SafetyReportProcessor extends Actor{
  override def receive: Receive = {
    case SafetyReport(report, reportDataReceiver) => {


      val requiredXMLFields = report.descendant.distinct.filter(_.child.length == 1)

      val fields = requiredXMLFields.filter( f =>
        f.label == "reactionmeddrapt" ||
        f.label == "medicinalproduct" ||
          f.label ==   "drugdosageform" ||
          f.label == "activesubstancename" ||
          f.label == "safetyreportid"
      ).map(f => f.label -> f.text).toMap

     val obj =  SafetyReportData(fields.getOrElse("reactionmeddrapt", ""),
       fields.getOrElse("medicinalproduct", ""),
       fields.getOrElse("drugdosageform", ""),
       fields.getOrElse("activesubstancename", ""),
       fields.getOrElse("safetyreportid", "")
      )
      println("data obj ======"+obj.activesubstancename)
      reportDataReceiver ! obj.activesubstancename
    }
  }
}

