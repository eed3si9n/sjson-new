import Dependencies._

lazy val root = (project in file(".")).
  aggregate(core, shapeless, shapelessTest,
    supportSpray,
    supportJson4s).
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
  enablePlugins(BoilerplatePlugin).
  settings(
    name := "sjson new core",
    libraryDependencies ++= testDependencies,
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
  )

lazy val shapeless = project.
  dependsOn(core).
  settings(
    name := "sjson new generic",
    libraryDependencies ++= shapelessDependencies.value,
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
  )

lazy val shapelessTest = (project in file("shapeless-test")).
  dependsOn(shapeless, supportSpray).
  settings(
    name := "sjson new shapeless test",
    publish := {},
    publishLocal := {},
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= shapelessDependencies.value
  )

def support(n: String) =
  Project(id = n, base = file(s"support/$n")).
    dependsOn(core).
    settings(
      name := s"sjson-new-$n",
      libraryDependencies ++= testDependencies,
      scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
    )

lazy val supportSpray = support("spray").
  settings(
    libraryDependencies += sprayJson
  )

lazy val supportJson4s = support("json4s").
  settings(
    libraryDependencies += json4sAst
  )
