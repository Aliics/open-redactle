import org.scalajs.linker.interface.ModuleSplitStyle
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val root = (project in file("."))
  .aggregate(webapp, server, shared.jvm, shared.js)

lazy val webapp = project.in(file("webapp"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared.js)
  .settings(
    scalaVersion := Versions.scala,
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
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
      "org.java-websocket" % "Java-WebSocket" % Versions.javaWebsocket,
      "org.slf4j" % "slf4j-simple" % Versions.slf4j,
      "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging,

      "org.scalatest" %%% "scalatest" % Versions.scalaTest % Test,
    ),
  )

lazy val scraper = project.in(file("scraper"))
  .dependsOn(shared.jvm)
  .settings(
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % Versions.jsoup,
      "com.softwaremill.sttp.client3" %% "core" % Versions.sttp,
    ),
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % Versions.upickle,
    ),
  )
