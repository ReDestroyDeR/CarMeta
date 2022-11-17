ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

val catsCoreVersion = "2.9.0"
val catsEffectVersion = "3.4.0"

val fs2Version = "3.3.0"

val kafkaStreamsVersion = "3.3.1"
val fs2KafkaStreamsVersion = "3.0.0-M8"

val scalaScraperVersion = "3.0.0"
val circeVersion = "0.15.0-M1"

val canoeVersion = "0.6.0"

lazy val root = (project in file("."))
  .settings(
    name := "CarMeta",
    idePackagePrefix := Some("ru.red.car_meta")
  )

// Modules

lazy val qualification = project
  .settings(
    name := "Qualification",
    idePackagePrefix := Some("ru.red.car_meta.qualification")
  )
  .dependsOn(root)

lazy val scraper = project
  .settings(
    name := "Scraper",
    idePackagePrefix := Some("ru.red.car_meta.scraper")
  )
  .dependsOn(root)

lazy val telegram = project
  .settings(
    name := "Telegram",
    idePackagePrefix := Some("ru.red.car_meta.telegram")
  )
  .dependsOn(root)

lazy val warehouse = project
  .settings(
    name := "Warehouse",
    idePackagePrefix := Some("ru.red.car_meta.warehouse")
  )
  .dependsOn(root)

// Typelevel

scalacOptions += "-Ypartial-unification"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsCoreVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.typelevel" %% "cats-effect-testkit" % catsEffectVersion % Test
)

// FS2

libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version

// Kafka

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaStreamsVersion,
  "com.github.fd4s" %% "fs2-kafka" % fs2KafkaStreamsVersion
)

// gRPC

enablePlugins(Fs2Grpc)
libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
)

// Scraping and Parsing

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % scalaScraperVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion
)

// Telegram

libraryDependencies += "org.augustjune" %% "canoe" % canoeVersion
