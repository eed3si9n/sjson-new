import Dependencies._
import com.typesafe.tools.mima.core._

val scala210 = "2.10.7"
val scala211 = "2.11.12"
val scala212 = "2.12.11"
val scala213 = "2.13.1"

ThisBuild / version := "0.8.3-SNAPSHOT"
ThisBuild / crossScalaVersions := Seq(scala210, scala211, scala212, scala213)
ThisBuild / scalaVersion := scala212

lazy val root = (project in file("."))
  .aggregate(core, // shapeless, shapelessTest,
    supportSpray,
    supportScalaJson,
    supportMsgpack,
    supportMurmurhash)
  .settings(
    name := "sjson new",
    publish / skip := true,
    crossScalaVersions := Nil,
  )

// WORKAROUND https://github.com/sbt/sbt/issues/3353
val scalaVersionSettings = Def settings (
  crossScalaVersions := Seq(scala210, scala211, scala212),
  scalaVersion := scala212
)

val mimaSettings = Def settings (
  mimaPreviousArtifacts := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v >= 13 =>
        Set.empty
      case _ =>
        Set(organization.value %% moduleName.value % "0.8.0")
    }
  }
)

lazy val core = project
  .enablePlugins(BoilerplatePlugin)
  .settings(
    crossScalaVersions += scala213,
    name := "sjson new core",
    libraryDependencies ++= testDependencies.value,
    scalacOptions ++= Seq("-Xfuture", "-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
    mimaSettings,
    mimaBinaryIssueFilters ++= Seq(
      // private[this] final val
      ProblemFilters.exclude[ReversedMissingMethodProblem]("sjsonnew.JavaExtraFormats.sjsonnew$JavaExtraFormats$$FileScheme")
    )
  )

def support(n: String) =
  Project(id = n, base = file(s"support/$n"))
    .dependsOn(core)
    .settings(
      name := s"sjson-new-$n",
      libraryDependencies ++= testDependencies.value,
      scalacOptions ++= Seq("-Xfuture", "-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
      mimaSettings
    )

lazy val supportSpray = support("spray").
  settings(
    libraryDependencies += sprayJson
  )

lazy val supportScalaJson = support("scalajson")
  .settings(
    libraryDependencies ++= Seq(scalaJson, jawnParser.value),
    Compile / unmanagedSourceDirectories ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v < 13 => List(baseDirectory.value / "src" / "main" / "scala-pre2.13")
        case _ => List()
      }
    }
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
    libraryDependencies ++= Seq(jawnSpray.value, lmIvy),
    crossScalaVersions --= Seq(scala212, scala213),
    javaOptions in (Jmh, run) ++= Seq("-Xmx1G", "-Dfile.encoding=UTF8"),
    publish / skip := true,
  )

ThisBuild / organization := "com.eed3si9n"
ThisBuild / organizationName := "eed3si9n"
ThisBuild / organizationHomepage := Some(url("http://eed3si9n.com/"))
ThisBuild / homepage := scmInfo.value map (_.browseUrl)
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/eed3si9n/sjson-new"), "git@github.com:eed3si9n/sjson-new.git"))
ThisBuild / developers := List(
  Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n"))
)
ThisBuild / description := "A Scala library for JSON (de)serialization"
ThisBuild / licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
