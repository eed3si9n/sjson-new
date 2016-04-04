import sbt._

object Dependencies {
  lazy val testDependencies = Seq(
    "org.specs2" %% "specs2-core" % "3.7.1" % "test",
    "org.specs2" %% "specs2-scalacheck" % "3.7.1" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )
  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.1"
  lazy val json4sAst = "org.json4s" %% "json4s-ast" % "3.3.0.RC1"
}
