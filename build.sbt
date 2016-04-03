lazy val root = (project in file(".")).
  aggregate(core,
    supportSpray).
  settings(
    name := "sjson new",
    publish := {},
    publishLocal := {},
    inThisBuild(List(
      organization := "com.eed3si9n",
      version := "0.1.0-SNAPSHOT",
      crossScalaVersions := Seq("2.10.6", "2.11.8"),
      scalaVersion := "2.11.8",
      description := "A Scala library for JSON (de)serialization",
      licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
    ))
  )

lazy val core = project.
  settings(
    name := "sjson new core",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.7.1" % "test",
      "org.specs2" %% "specs2-scalacheck" % "3.7.1" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
    ),
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
  )

def support(n: String) =
  Project(id = n, base = file(s"support/$n")).
    dependsOn(core).
    settings(
      name := s"sjson-new-$n-support",
      libraryDependencies += "io.spray" %% "spray-json" % "1.3.1",
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2-core" % "3.7.1" % "test",
        "org.specs2" %% "specs2-scalacheck" % "3.7.1" % "test",
        "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
      ),
      scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
    )

lazy val supportSpray = support("spray")
