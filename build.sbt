name := "runtime-scaladoc-reader"

organization := "com.github.takezoe"

version := "1.0.0"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value
)