import org.scalajs.linker.interface.ModuleSplitStyle

lazy val webapp = project.in(file("webapp"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "3.3.1",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    },

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0",
      "com.raquo" %%% "laminar" % "15.0.1",
    ),
  )

lazy val server = project.in(file("server"))
  .settings(
    scalaVersion := "3.3.1",
  )
