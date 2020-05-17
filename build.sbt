name := "AkkaSparkApp"

version := "0.1"

scalaVersion := "2.11.1"

//lazy val akkaVersion = "2.6.5"

libraryDependencies ++= Seq(
  //"com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % "2.5.31",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
//"org.scala-lang.modules" %% "scala-xml" % "1.2.0"
)

libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.4.5"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.4.5"

