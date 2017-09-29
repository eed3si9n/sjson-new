import Dependencies._

val scala210 = "2.10.6"
val scala211 = "2.11.11"
val scala212 = "2.12.2"

lazy val root = (project in file("."))
  .aggregate(core, // shapeless, shapelessTest,
    supportSpray,
    supportScalaJson,
    supportMsgpack,
    supportMurmurhash)
  .settings(
    commonSettings,
    name := "sjson new",
    noPublish,
    inThisBuild(Def settings (
      organization := "com.eed3si9n",
      organizationName := "eed3si9n",
      organizationHomepage := Some(url("http://eed3si9n.com/")),
      homepage := scmInfo.value map (_.browseUrl),
      scmInfo := Some(ScmInfo(url("https://github.com/eed3si9n/sjson-new"), "git@github.com:eed3si9n/sjson-new.git")),
      developers := List(
        Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n"))
      ),
      version := "0.8.2-SNAPSHOT",
      isSnapshot := (isSnapshot or version(_ endsWith "-SNAPSHOT")).value,
      scalaVersionSettings,
      description := "A Scala library for JSON (de)serialization",
      licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
        else Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    ))
  )

// WORKAROUND https://github.com/sbt/sbt/issues/3353
val scalaVersionSettings = Def settings (
  crossScalaVersions := Seq(scala210, scala211, scala212),
  scalaVersion := scala212
)

val commonSettings = scalaVersionSettings

val noPublish = List(
  publish := {},
  publishLocal := {},
  PgpKeys.publishSigned := {},
  publishArtifact in Test := false,
  publishArtifact in Compile := false
)

val mimaSettings = Def settings (
  mimaPreviousArtifacts := Set(organization.value %% moduleName.value % "0.8.0")
)

lazy val core = project
  .enablePlugins(BoilerplatePlugin)
  .settings(
    commonSettings,
    name := "sjson new core",
    libraryDependencies ++= testDependencies,
    scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
    mimaSettings
  )

def support(n: String) =
  Project(id = n, base = file(s"support/$n"))
    .dependsOn(core)
    .settings(
      commonSettings,
      name := s"sjson-new-$n",
      libraryDependencies ++= testDependencies,
      scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
      mimaSettings
    )

lazy val supportSpray = support("spray").
  settings(
    libraryDependencies += sprayJson
  )

lazy val supportScalaJson = support("scalajson")
  .settings(
    libraryDependencies ++= Seq(scalaJson, jawnParser)
  )

lazy val supportMsgpack = support("msgpack")
  .settings(
    libraryDependencies += msgpackCore
  )

lazy val supportMurmurhash = support("murmurhash")

lazy val benchmark = (project in file("benchmark"))
  .dependsOn(supportSpray, supportScalaJson, supportMsgpack)
  .enablePlugins(JmhPlugin)
  .settings(
    // commonSettings, // TODO: Fix running benchmarks on all target versions
    libraryDependencies ++= Seq(jawnSpray, lm),
    crossScalaVersions -= scala212,
    javaOptions in (Jmh, run) ++= Seq("-Xmx1G", "-Dfile.encoding=UTF8"),
    noPublish
  )
