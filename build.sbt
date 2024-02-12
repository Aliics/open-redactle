import org.scalajs.linker.interface.ModuleSplitStyle
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

ThisBuild / scalaVersion := Versions.scala
ThisBuild / assemblyMergeStrategy := { _ => MergeStrategy.first }

lazy val root = (project in file("."))
  .aggregate(webapp, server, shared.jvm, shared.js)

lazy val webapp = project.in(file("webapp"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared.js)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("webapp")),
        )
    },

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % Versions.scalaJs,
      "com.raquo" %%% "laminar" % Versions.laminar,
    ),
  )

lazy val server = project.in(file("server"))
  .dependsOn(shared.jvm)
  .settings(
    assembly / assemblyJarName := "server-assembly.jar",
    libraryDependencies ++= Seq(
      "org.java-websocket" % "Java-WebSocket" % Versions.javaWebsocket,
      "software.amazon.awssdk" % "cloudwatch" % Versions.awsSdk,

      "org.scalatest" %% "scalatest" % Versions.scalaTest % Test,
    ),
  )

lazy val scraper = project.in(file("scraper"))
  .dependsOn(shared.jvm)
  .settings(
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % Versions.jsoup,
      "com.softwaremill.sttp.client3" %% "core" % Versions.sttp,
    ),
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % Versions.upickle,
    ),
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-simple" % Versions.slf4j,
      "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging,
      "software.amazon.awssdk" % "s3" % Versions.awsSdk,

      "org.scalatest" %% "scalatest" % Versions.scalaTest % Test,
    ),
  )
