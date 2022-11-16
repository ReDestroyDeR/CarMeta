ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "CarMeta",
    idePackagePrefix := Some("ru.red.car_meta")
  )

lazy val qualification = project
  .settings(
    name := "Qualification",
    idePackagePrefix := Some("ru.red.car_meta.qualification")
  )
lazy val scraper = project
  .settings(
    name := "Scraper",
    idePackagePrefix := Some("ru.red.car_meta.scraper")
  )
lazy val telegram = project
  .settings(
    name := "Telegram",
    idePackagePrefix := Some("ru.red.car_meta.telegram")
  )
lazy val warehouse = project
  .settings(
    name := "Warehouse",
    idePackagePrefix := Some("ru.red.car_meta.warehouse")
  )
