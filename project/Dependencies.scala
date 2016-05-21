import sbt._
import Keys._

object Dependencies {
  lazy val testDependencies = Seq(
    "org.specs2" %% "specs2-core" % "3.7.1" % "test",
    "org.specs2" %% "specs2-scalacheck" % "3.7.1" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.1"
  lazy val json4sAst = "org.json4s" %% "json4s-ast" % "3.3.0.RC1"
  lazy val shaplessVersion = "2.3.1"
  lazy val shapelessDependencies = Def.setting {
    if (scalaVersion.value startsWith "2.10.") Seq(
      "com.chuusai" %% "shapeless" % shaplessVersion,
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
    else Seq("com.chuusai" %% "shapeless" % shaplessVersion)
  }
}
