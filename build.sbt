import org.scalajs.linker.interface.{ESVersion, ModuleKind}
import org.scalajs.sbtplugin.ScalaJSPlugin

ThisBuild / version := "1.0.0"
ThisBuild / organization := "com.anjunar"
ThisBuild / organizationName := "Anjunar"
ThisBuild / organizationHomepage := Some(url("https://github.com/anjunar"))
ThisBuild / scalaVersion := "3.8.3"

ThisBuild / homepage := Some(url("https://github.com/anjunar/scalajs-lexical"))
ThisBuild / description := "Lexical editor wrapper for Scala.js."
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/anjunar/scalajs-lexical"),
    "scm:git:https://github.com/anjunar/scalajs-lexical.git",
    Some("scm:git:git@github.com:anjunar/scalajs-lexical.git")
  )
)
ThisBuild / developers := List(
  Developer(
    id = "anjunar",
    name = "Patrick Bittner",
    email = "anjunar@gmx.de",
    url = url("https://github.com/anjunar")
  )
)

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := {
  if (isSnapshot.value) {
    Some("central-snapshots" at "https://central.sonatype.com/repository/maven-snapshots/")
  } else {
    localStaging.value
  }
}

lazy val commonSettings = Seq(
  scalaJSLinkerConfig ~= (
    _.withModuleKind(ModuleKind.ESModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2021))
    ),
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.1",
  libraryDependencies += ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13),
  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.19" % Test
)

lazy val library = (project in file("library"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "scalajs-lexical",
    moduleName := "scalajs-lexical",
    commonSettings,
    publishMavenStyle := true
  )

lazy val codemirror = (project in file("codemirror"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(library)
  .settings(
    name := "lexical-codemirror",
    moduleName := "lexical-codemirror",
    commonSettings,
    publishMavenStyle := true
  )

lazy val application = Project(id = "scalajs-lexical-demo", base = file("application"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(library, codemirror)
  .settings(
    name := "lexical-demo",
    commonSettings,
    scalaJSUseMainModuleInitializer := true,
    publish / skip := true
  )

lazy val root = (project in file("."))
  .aggregate(library, codemirror, application)
  .settings(
    publish / skip := true
  )
