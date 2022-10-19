import Dependencies._
import com.typesafe.tools.mima.core._
import sbt.internal.ProjectMatrix

val scala212 = "2.12.15"
val scala213 = "2.13.8"
val scala3 = "3.1.0"

ThisBuild / version := "0.12.0-SNAPSHOT"
ThisBuild / scalaVersion := scala212
lazy val allScalaVersions = Seq(scala212, scala213, scala3)

lazy val root = (project in file("."))
  .aggregate(core.projectRefs ++
    supportSpray.projectRefs ++
    supportScalaJson.projectRefs ++
    supportMsgpack.projectRefs ++
    supportMurmurhash.projectRefs: _*)
  .settings(
    name := "sjson new",
    publish / skip := true,
    crossScalaVersions := Nil,
    mimaPreviousArtifacts := Set.empty,
  )

val mimaSettings = Def settings (
  mimaPreviousArtifacts := Set.empty,
)

lazy val core = (projectMatrix in file("core"))
  .enablePlugins(BoilerplatePlugin)
  .settings(
    name := "sjson new core",
    scalacOptions ++= Seq("-Xfuture", "-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
    mimaSettings,
    mimaBinaryIssueFilters ++= Seq(
      // private[this] final val
      ProblemFilters.exclude[ReversedMissingMethodProblem]("sjsonnew.JavaExtraFormats.sjsonnew$JavaExtraFormats$$FileScheme")
    )
  )
  .jvmPlatform(scalaVersions = allScalaVersions, settings = Seq(
    libraryDependencies ++= testDependencies.value,
  ))

def support(n: String) =
  ProjectMatrix(id = n, base = file(s"support/$n"))
    .dependsOn(core)
    .settings(
      name := s"sjson-new-$n",
      scalacOptions ++= Seq("-Xfuture", "-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8"),
      mimaSettings,
    )

lazy val supportSpray = support("spray")
  .jvmPlatform(scalaVersions = allScalaVersions, settings = Seq(
    libraryDependencies ++= testDependencies.value ++ Seq(sprayJson),
  ))

lazy val supportScalaJson = support("scalajson")
  .settings(
    mimaBinaryIssueFilters ++= Seq(
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("sjsonnew.support.scalajson.unsafe.CompactPrinter.*"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("sjsonnew.support.scalajson.unsafe.JsonPrinter.*"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("sjsonnew.support.scalajson.unsafe.PrettyPrinter.*"),
      ProblemFilters.exclude[ReversedMissingMethodProblem]("sjsonnew.support.scalajson.unsafe.CompactPrinter.*"),
      ProblemFilters.exclude[ReversedMissingMethodProblem]("sjsonnew.support.scalajson.unsafe.JsonPrinter.*"),
      ProblemFilters.exclude[ReversedMissingMethodProblem]("sjsonnew.support.scalajson.unsafe.PrettyPrinter.*"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("sjsonnew.support.scalajson.unsafe.Parser.async"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("sjsonnew.support.scalajson.unsafe.Parser.facade"),
      ProblemFilters.exclude[MissingTypesProblem]("sjsonnew.support.scalajson.unsafe.Parser$"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("sjsonnew.support.scalajson.unsafe.Parser.async"),
    )
  )
  .jvmPlatform(scalaVersions = allScalaVersions, settings = Seq(
    libraryDependencies ++= testDependencies.value ++ Seq(scalaJson, shadedJawnParser),
  ))

lazy val supportMsgpack = support("msgpack")
  .jvmPlatform(scalaVersions = allScalaVersions, settings = Seq(
    libraryDependencies ++= testDependencies.value ++ Seq(msgpackCore),
  ))

lazy val supportMurmurhash = support("murmurhash")
  .jvmPlatform(scalaVersions = allScalaVersions, settings = Seq(
    libraryDependencies ++= testDependencies.value,
  ))

lazy val benchmark = (projectMatrix in file("benchmark"))
  .dependsOn(supportSpray, supportScalaJson, supportMsgpack)
  .enablePlugins(JmhPlugin)
  .settings(
    libraryDependencies ++= Seq(jawnSpray, lmIvy),
    Jmh / run / javaOptions ++= Seq("-Xmx1G", "-Dfile.encoding=UTF8"),
    publish / skip := true,
  )
  .jvmPlatform(scalaVersions = Seq(scala212))

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
ThisBuild / testFrameworks += new TestFramework("verify.runner.Framework")
