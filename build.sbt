import org.scalajs.linker.interface.{ESVersion, ModuleKind}
import org.scalajs.sbtplugin.ScalaJSPlugin

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.8.3"

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
    name := "lexical-library",
    commonSettings
  )

lazy val codemirror = (project in file("codemirror"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(library)
  .settings(
    name := "lexical-codemirror",
    commonSettings
  )

lazy val application = (project in file("application"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(library, codemirror)
  .settings(
    name := "lexical-demo",
    commonSettings,
    scalaJSUseMainModuleInitializer := true
  )

lazy val root = (project in file("."))
  .aggregate(library, codemirror, application)
  .settings(
    publish / skip := true
  )
