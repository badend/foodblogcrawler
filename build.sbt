import _root_.sbtassembly.AssemblyPlugin.autoImport._
import _root_.sbtassembly.PathList
import sbt.Keys._
import sbt._
import com.github.play2war.plugin._
// put this at the top of the file
lazy val root = (project in file(".")).enablePlugins(PlayScala)


name := "foodblogcrawler"

version := "0.1.0-SNAPSHOT"

organization := "net.badend"

scalaVersion := "2.10.6"

crossScalaVersions := Seq("2.10.6", "2.11.4")

aggregate in runMain := true

val sprayV = "1.3.1"

val hadoopversion = "2.0.0-cdh4.6.0"

assemblyMergeStrategy in assembly :=  {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}


libraryDependencies ++= Seq(
  "org.scala-lang" %% "scala-pickling" % "0.9.0",
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "io.spray" % "spray-client" % sprayV,
  "com.google.code.findbugs" % "jsr305" % "2.0.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.google.guava" % "guava" % "18.0",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-ext" % "3.2.10",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0",
  "org.jsoup" % "jsoup" % "1.8.1",
  "com.alibaba" % "fastjson" % "1.2.1",
  "com.github.nscala-time" %% "nscala-time" % "1.4.0",
  "com.twitter.penguin" % "korean-text" % "1.0",
  "mysql" % "mysql-connector-java" % "5.1.34",
"com.sksamuel.elastic4s" % "elastic4s_2.10" % "1.4.13",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.4.2",
"com.fasterxml.jackson.core" % "jackson-databind" % "2.4.2",
"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.2",
  "org.scalatest" % "scalatest_2.10" % "2.1.6" % "test")


resolvers ++= Seq(  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "cdh-releases-rcs" at "https://repository.cloudera.com/content/groups/cdh-releases-rcs",
  "public-cloudera" at "https://repository.cloudera.com/content/groups/public",
  "Scala Tools Snapshots2" at "https://oss.sonatype.org/content/groups/scala-tools",
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",
  "twitter" at "http://maven.twttr.com/",
  "spray repo" at "http://repo.spray.io/",
  "spray night" at "http://nightlies.spray.io"
)

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"


javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

retrieveManaged := true

publishMavenStyle := true
