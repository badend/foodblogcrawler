import sbt.Keys._
import sbt._
import com.github.play2war.plugin._
// put this at the top of the file
lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := "foodblogcrawler"

version := "0.1.0-SNAPSHOT"

organization := "net.badend"

scalaVersion := "2.10.4"

aggregate in runMain := true

val sprayV = "1.3.1"


Play2WarKeys.servletVersion := "3.0"


Play2WarPlugin.play2WarSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings

val hadoopversion = "2.0.0-cdh4.6.0"

libraryDependencies ++= Seq(
  ws, // Play's web services module
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopversion exclude("commons-daemon", "commons-daemon"),
  "org.apache.hadoop" % "hadoop-auth" % hadoopversion exclude("commons-daemon", "commons-daemon"),
  "org.apache.hadoop" % "hadoop-core" % "2.0.0-mr1-cdh4.6.0" exclude("commons-daemon", "commons-daemon"),
  "org.apache.hadoop" % "hadoop-client" % hadoopversion exclude("commons-daemon", "commons-daemon"),
  "org.apache.hive" % "hive-jdbc" % "0.10.0-cdh4.4.0" exclude("commons-daemon", "commons-daemon"),
  "org.scala-lang" %% "scala-pickling" % "0.8.0",
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "com.oracle" % "ojdbc6" % "11.2.0",
  "io.spray" % "spray-can" % sprayV,
  "io.spray" % "spray-client" % sprayV,
  "io.spray" % "spray-caching" % sprayV,
  "io.spray" % "spray-routing" % sprayV,
  "io.spray" % "spray-client" % sprayV,
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4",
  "com.google.code.findbugs" % "jsr305" % "2.0.3",
  "c3p0" % "c3p0" % "0.9.1.2",
  "redis.clients" % "jedis" % "2.2.1",
  "org.webjars" % "bootstrap" % "2.3.1",
  "org.webjars" % "flot" % "0.8.0",
  "com.google.guava" % "guava" % "18.0",
  "mysql" % "mysql-connector-java" % "5.1.32",
  "com.alibaba" % "fastjson" % "1.1.41",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "org.json4s" %% "json4s-ext" % "3.2.10",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0",
  "org.jsoup" % "jsoup" % "1.8.1",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "org.scalatestplus" % "play_2.10" % "1.0.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test"
)


resolvers ++= Seq(    "sqt" at "http://maven.daumcorp.com/content/repositories/daum-sqt-group",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
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


javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

retrieveManaged := true

//publishTo := Some("daum" at "http://maven.daumcorp.com/content/groups/daum-sqt-group/")

publishMavenStyle := true


publishTo := Some("daum snapshot" at "http://maven.daumcorp.com/content/repositories/daum-sqt-snapshots")


pomExtra := (
  // <distributionManagement>
  //        <repository>
  //                <id>daum</id>
  //                <name>Daum Repository</name>
  //                <url>http://maven.daumcorp.com/content/repositories/daum</url>
  //        </repository>
  //        <snapshotRepository>
  //                <id>daum-snapshots</id>
  //                <name>Daum Snapshot Repository</name>
  //                <url>http://maven.daumcorp.com/content/repositories/daum-snapshots</url>
  //        </snapshotRepository>
  //    </distributionManagement>
  <scm>
    <url>http://digit.daumcorp.com/badend/arfapi</url>
    <connection>scm:git:git@dgit.co:badend/arfapi.git</connection>
  </scm>
    <developers>
      <developer>
        <id>badend</id>
        <name>badend</name>
      </developer>
    </developers>)
