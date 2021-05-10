name := """MongoDB-scala-example"""

version := "1.0"

libraryDependencies ++= Seq(
//  "org.reactivemongo" %% "reactivemongo" % "1.0.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-slf4j"%"2.5.31",
//  "org.reactivemongo" %% "reactivemongo-test" % "1.0.0",
//"org.reactivemongo" %% "reactivemongo-scalafix" % "1.0.3",
//  "org.reactivemongo" %% "reactivemongo-bson-compat" % "0.20.13"
)
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.13-play28",
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.1-play28",
  "org.reactivemongo" %% "reactivemongo-bson-compat" % "0.20.13",
  "com.typesafe.play" %% "play-json-joda" % "2.7.4"
)
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
  "org.json4s" %% "json4s-native" % "3.6.11",

  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"

)
