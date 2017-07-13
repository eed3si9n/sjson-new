import sbt._
import Keys._

object Dependencies {
  lazy val testDependencies = Seq(
    "org.specs2" %% "specs2-core" % "3.8.6" % "test",
    "org.specs2" %% "specs2-scalacheck" % "3.8.6" % "test",
    "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.2"
  lazy val scalaJson = "com.eed3si9n" %% "shaded-scalajson" % "1.0.0-M4"
  lazy val msgpackCore = "org.msgpack" % "msgpack-core" % "0.8.11"
  lazy val jawnVersion = "0.10.4"
  lazy val jawnParser = "org.spire-math" %% "jawn-parser" % jawnVersion
  lazy val jawnSpray = "org.spire-math" %% "jawn-spray" % jawnVersion
  lazy val lm = "org.scala-sbt" %% "librarymanagement" % "0.1.0-M12"
  // lazy val shaplessVersion = "2.3.0"
  // lazy val shapelessDependencies = Def.setting {
  //   if (scalaVersion.value startsWith "2.10.") Seq(
  //     "com.chuusai" %% "shapeless" % shaplessVersion,
  //     compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
  //   else Seq("com.chuusai" %% "shapeless" % shaplessVersion)
  // }
}
