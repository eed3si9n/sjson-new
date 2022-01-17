import sbt._
import Keys._

object Dependencies {
  lazy val testDependencies = Def.setting(Seq(
    verify % Test,
    scalacheck % Test,
    scalatest % Test,
  ))
  def scalaCheckVersion = "1.15.4"
  lazy val scalacheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  def scalaTestVersion = "3.2.10"
  lazy val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion
  def verifyVersion = "1.0.0"
  lazy val verify = "com.eed3si9n.verify" %% "verify" % verifyVersion
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.6"
  lazy val scalaJson = "com.eed3si9n" %% "shaded-scalajson" % "1.0.0-M4"
  lazy val msgpackCore = "org.msgpack" % "msgpack-core" % "0.8.17"
  lazy val jawnVersion = "1.0.0"
  lazy val jawnSpray = "org.typelevel" %% "jawn-spray" % jawnVersion
  lazy val shadedJawnParser = "com.eed3si9n" %% "shaded-jawn-parser" % "1.3.2"
  lazy val lmIvy = "org.scala-sbt" %% "librarymanagement-ivy" % "1.2.4"
}
