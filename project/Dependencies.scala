import sbt._
import Keys._

object Dependencies {
  val specs2Version = Def.setting {
    "4.12.12"
  }
  lazy val testDependencies = Def.setting(Seq(
    "org.specs2" %% "specs2-core" % specs2Version.value % "test",
    "org.specs2" %% "specs2-scalacheck" % specs2Version.value % "test",
    "org.scalacheck" %% "scalacheck" % "1.15.4" % "test",
    "org.scalatest" %% "scalatest" % "3.2.10" % "test"
  ))
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.5"
  lazy val scalaJson = "com.eed3si9n" %% "shaded-scalajson" % "1.0.0-M4"
  lazy val msgpackCore = "org.msgpack" % "msgpack-core" % "0.8.17"
  lazy val jawnVersion = "1.0.0"
  lazy val jawnParser = "org.typelevel" %% "jawn-parser" % jawnVersion
  lazy val jawnSpray = "org.typelevel" %% "jawn-spray" % jawnVersion
  lazy val lmIvy = "org.scala-sbt" %% "librarymanagement-ivy" % "1.2.4"
  // lazy val shaplessVersion = "2.3.0"
  // lazy val shapelessDependencies = Def.setting {
  //   if (scalaVersion.value startsWith "2.10.") Seq(
  //     "com.chuusai" %% "shapeless" % shaplessVersion,
  //     compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
  //   else Seq("com.chuusai" %% "shapeless" % shaplessVersion)
  // }
}
