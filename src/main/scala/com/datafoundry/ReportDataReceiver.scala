package com.datafoundry

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver.Receiver

/*object ReportDataReceiver{
  def props = Props(new ReportDataReceiver)
}
class ReportDataReceiver extends ActorReceiver {
  def receive = {
    case data: String => store(data)
  }
}*/
object ReportDataReceiver{
  var actorSystem:ActorSystem = _
  def setActorSystem(system: ActorSystem) = {actorSystem = system}
}

class ReportDataReceiver extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging {
  var messageReceiverActor = ActorRef.noSender
  def getMessageReceiverActorRef = messageReceiverActor

  override def onStart(): Unit = {
    messageReceiverActor  = ReportDataReceiver.actorSystem.actorOf(Props(new MessageReceiverActor), "messageReceiverActor")
  }
  override def onStop(): Unit = {}
  class MessageReceiverActor extends Actor {
    override def receive: Receive = {
      case data:String => {
        println("data received---------------------")
        store(data)
      }
    }
  }
}
