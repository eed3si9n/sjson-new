import sbt._
import Keys._

object Dependencies {
  lazy val testDependencies = Def.setting(Seq(
    verify % Test,
    "org.scalacheck" %% "scalacheck" % "1.15.4" % Test,
    "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  ))
  lazy val verify = "com.eed3si9n.verify" %% "verify" % "1.0.0"
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.6"
  lazy val scalaJson = "com.eed3si9n" %% "shaded-scalajson" % "1.0.0-M4"
  lazy val msgpackCore = "org.msgpack" % "msgpack-core" % "0.8.17"
  lazy val jawnVersion = "1.0.0"
  lazy val jawnSpray = "org.typelevel" %% "jawn-spray" % jawnVersion
  lazy val shadedJawnParser = "com.eed3si9n" %% "shaded-jawn-parser" % "1.3.2"
  lazy val lmIvy = "org.scala-sbt" %% "librarymanagement-ivy" % "1.2.4"
}
