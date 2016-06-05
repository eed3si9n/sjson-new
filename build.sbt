import Dependencies._

lazy val root = (project in file(".")).
  aggregate(core, // shapeless, shapelessTest,
    binary,
    supportSpray,
    supportScalaJson,
    supportMsgpack).
  settings(
    name := "sjson new",
    publish := {},
    publishLocal := {},
    PgpKeys.publishSigned := {},
    publishArtifact in Test := false,
    publishArtifact in Compile := false,
    inThisBuild(List(
      organization := "com.eed3si9n",
      organizationName in ThisBuild := "eed3si9n",
      organizationHomepage in ThisBuild := Some(url("http://eed3si9n.com/")),
      homepage in ThisBuild := Some(url("https://github.com/eed3si9n/sjson-new")),
      scmInfo in ThisBuild := Some(ScmInfo(url("https://github.com/eed3si9n/sjson-new"), "git@github.com:eed3si9n/sjson-new.git")),
      developers in ThisBuild := List(
        Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n"))
      ),
      version := "0.4.0",
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

lazy val supportScalaJson = support("scalajson").
  settings(
    libraryDependencies ++= Seq(scalaJson, jawnParser)
  )

lazy val supportMsgpack = support("msgpack").
  settings(
    libraryDependencies += msgpackCore
  )

lazy val binary = (project in file("binary")).
  dependsOn(core).
  settings(
    commonSettings,
    name := "sjson new binary",
    libraryDependencies ++= scalaTestDependencies,
    libraryDependencies += sprayJson % Test,
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
    initialCommands in console := "import sjsonnew.binary._, BUtil._"
  )

lazy val benchmark = (project in file("benchmark")).
  dependsOn(supportSpray, supportScalaJson, supportMsgpack, binary).
  enablePlugins(JmhPlugin).
  settings(
    libraryDependencies ++= Seq(jawnSpray, lm),
    javaOptions in (Jmh, run) ++= Seq("-Xmx1G", "-Dfile.encoding=UTF8"),
    publish := {},
    publishLocal := {},
    PgpKeys.publishSigned := {}
  )
