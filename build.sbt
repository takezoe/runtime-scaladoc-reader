name := "runtime-scaladoc-reader"

organization := "com.github.takezoe"

version := "1.0.2"

crossScalaVersions := Seq("2.13.3", "2.12.12")
scalaVersion := crossScalaVersions.value.head

libraryDependencies ++= Seq(
  scalaOrganization.value % "scala-compiler" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
Test / scalacOptions ++= {
  val jar = (Compile / packageBin).value
  Seq(s"-Xplugin:${jar.getAbsolutePath}", s"-Jdummy=${jar.lastModified}") // ensures recompile
}
Test / scalacOptions += "-Yrangepos"
Compile / console / scalacOptions := Seq("-language:_", "-Xplugin:" + (Compile / packageBin).value)
Test / console / scalacOptions := (Compile / console / scalacOptions).value
Test / fork := true

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/takezoe/runtime-scaladoc-reader</url>
    <licenses>
      <license>
        <name>The Apache Software License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/takezoe/runtime-scaladoc-reader</url>
      <connection>scm:git:https://github.com/takezoe/runtime-scaladoc-reader.git</connection>
    </scm>
    <developers>
      <developer>
        <id>takezoe</id>
        <name>Naoki Takezoe</name>
        <email>takezoe_at_gmail.com</email>
        <timezone>+9</timezone>
      </developer>
    </developers>)
