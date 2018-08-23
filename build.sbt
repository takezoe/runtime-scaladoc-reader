name := "runtime-scaladoc-reader"

organization := "com.github.takezoe"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value
)