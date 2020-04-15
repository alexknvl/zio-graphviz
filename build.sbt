organization := "com.alexknvl"
name         := "zio-graphviz"
version      := "0.1.0"
licenses     += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion in Global := "2.12.10"
crossScalaVersions := List("2.12.10", "2.13.1")

autoAPIMappings in ThisBuild := true

libraryDependencies ++= List(
  Dependencies.zio,
  Dependencies.zioProcess,
  Dependencies.scalatest  % Test,
  Dependencies.scalacheck % Test)