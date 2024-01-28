import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val webapp = project.in(file("webapp"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJs)
  .settings(
    scalaVersion := Versions.scala,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    },

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % Versions.scalaJs,
      "com.raquo" %%% "laminar" % Versions.laminar,
    ),
  )

lazy val server = project.in(file("server"))
  .dependsOn(sharedJvm)
  .settings(
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
      "org.java-websocket" % "Java-WebSocket" % Versions.javaWebsocket,
      "org.slf4j" % "slf4j-simple" % Versions.slf4j,
      "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging,
    ),
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % Versions.upickle,
    ),
  )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm
