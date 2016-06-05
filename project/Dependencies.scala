import sbt._
import Keys._

object Dependencies {
  lazy val testDependencies = Seq(
    "org.specs2" %% "specs2-core" % "3.7.1" % "test",
    "org.specs2" %% "specs2-scalacheck" % "3.7.1" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.1"
  lazy val scalaJson = "org.mdedetrich" %% "scala-json-ast" % "1.0.0-M1"
  lazy val msgpackCore = "org.msgpack" % "msgpack-core" % "0.8.7"
  lazy val jawnVersion = "0.8.4"
  lazy val jawnParser = "org.spire-math" %% "jawn-parser" % jawnVersion
  lazy val jawnSpray = "org.spire-math" %% "jawn-spray" % jawnVersion
  lazy val lm = "org.scala-sbt" %% "librarymanagement" % "0.1.0-M11"
  // lazy val shaplessVersion = "2.3.0"
  // lazy val shapelessDependencies = Def.setting {
  //   if (scalaVersion.value startsWith "2.10.") Seq(
  //     "com.chuusai" %% "shapeless" % shaplessVersion,
  //     compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
  //   else Seq("com.chuusai" %% "shapeless" % shaplessVersion)
  // }
}
