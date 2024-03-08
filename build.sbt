ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "postgres-helper"
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play"       %% "play-jdbc" % "2.8.19",
      "org.postgresql"          % "postgresql" % "42.5.4",
      "org.playframework.anorm" %% "anorm"     % "2.6.10",
    )
  )
