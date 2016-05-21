import Dependencies._

lazy val root = (project in file(".")).
  aggregate(core, // shapeless, shapelessTest,
    supportSpray,
    supportJson4s).
  settings(
    name := "sjson new",
    publish := {},
    publishLocal := {},
    inThisBuild(List(
      organization := "com.eed3si9n",
      organizationName in ThisBuild := "eed3si9n",
      organizationHomepage in ThisBuild := Some(url("http://eed3si9n.com/")),
      homepage in ThisBuild := Some(url("https://github.com/eed3si9n/sjson-new")),
      scmInfo in ThisBuild := Some(ScmInfo(url("https://github.com/eed3si9n/sjson-new"), "git@github.com:eed3si9n/sjson-new.git")),
      developers in ThisBuild := List(
        Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n"))
      ),
      version := "0.1.0",
      crossScalaVersions := Seq("2.10.6", "2.11.8"),
      scalaVersion := "2.11.8",
      description := "A Scala library for JSON (de)serialization",
      licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
    ))
  )

val commonSettings = List(
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val core = project.
  enablePlugins(BoilerplatePlugin).
  settings(
    commonSettings,
    name := "sjson new core",
    libraryDependencies ++= testDependencies,
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
  )

// shapeless integration is not done.
lazy val shapeless = project.
  dependsOn(core).
  settings(
    commonSettings,
    name := "sjson new generic",
    libraryDependencies ++= shapelessDependencies.value,
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
  )

lazy val shapelessTest = (project in file("shapeless-test")).
  dependsOn(shapeless, supportSpray).
  settings(
    commonSettings,
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
      commonSettings,
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
