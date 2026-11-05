import org.typelevel.sbt.NoPublishPlugin

val scala_212 = "2.12.21"
val scala_213 = "2.13.18"
val scala_3   = "3.3.9"

val V = new {
  val cats           = "2.13.0"
  val catsEffect     = "3.7.1"
  val fs2            = "3.14.0"
  val log4cats       = "2.8.0"
  val log4s          = "1.10.0"
  val scalaCheck     = "1.20.0"
  val scalaTest      = "3.2.20"
  val scalaJavaTime  = "2.6.0"
  val scalaJSJUL     = "1.0.0"
  val scalaNativeJUL = "1.0.0"
  val scribe         = "3.15.2"
  val zio            = "2.1.26"
}

val D = new {
  lazy val `cats-core`        = Def.setting("org.typelevel" %%% "cats-core" % V.cats)
  lazy val `cats-effect`      = Def.setting("org.typelevel" %%% "cats-effect" % V.catsEffect)
  lazy val `fs2-core`         = Def.setting("co.fs2" %%% "fs2-core" % V.fs2)
  lazy val `log4cats-core`    = Def.setting("org.typelevel" %%% "log4cats-core" % V.log4cats)
  lazy val `log4cats-testing` = Def.setting("org.typelevel" %%% "log4cats-testing" % V.log4cats)
  lazy val log4s              = Def.setting("org.log4s" %%% "log4s" % V.log4s)
  lazy val `log4s-testing`    = Def.setting("org.log4s" %%% "log4s-testing" % V.log4s)
  lazy val scalacheck         = Def.setting("org.scalacheck" %%% "scalacheck" % V.scalaCheck)
  lazy val `scala-java-time`  =
    Def.setting("io.github.cquiroz" %%% "scala-java-time" % V.scalaJavaTime)
  lazy val `scalajs-java-logging` =
    Def.setting("org.scala-js" %%% "scalajs-java-logging" % V.scalaJSJUL)
  lazy val `scala-native-java-logging` =
    Def.setting("org.scala-native" %%% "scala-native-java-logging" % V.scalaNativeJUL)
  lazy val scalatest = Def.setting("org.scalatest" %%% "scalatest" % V.scalaTest)
  lazy val scribe    = Def.setting("com.outr" %%% "scribe" % V.scribe)
  lazy val zio       = Def.setting("dev.zio" %%% "zio" % V.zio)
}

ThisBuild / tlBaseVersion              := "0.20"
ThisBuild / tlCiReleaseBranches        := Seq("master")
ThisBuild / tlVersionIntroduced        := Map("3" -> "0.16.3")
ThisBuild / organization               := "io.laserdisc"
ThisBuild / organizationName           := "LaserDisc"
ThisBuild / licenses                   := Seq(License.MIT)
ThisBuild / startYear                  := Some(2018)
ThisBuild / developers                 := List(tlGitHubDev("barambani", "Filippo Mariotti"))
ThisBuild / crossScalaVersions         := Seq(scala_212, scala_213, scala_3)
ThisBuild / scalaVersion               := scala_213
ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17"),
  JavaSpec.temurin("21"),
  JavaSpec.temurin("25")
)
// JS and Native artifacts don't depend on the JDK, so build them on the first JDK only
ThisBuild / githubWorkflowBuildMatrixExclusions := {
  val javas = (ThisBuild / githubWorkflowJavaVersions).value.tail
  for {
    java    <- javas
    project <- Seq("log-effectJS", "log-effectNative")
  } yield MatrixExclude(Map("java" -> java.render, "project" -> project))
}
ThisBuild / Test / parallelExecution := false

ThisBuild / githubWorkflowBuildPostamble += WorkflowStep.Sbt(
  List(
    "nativeConsumer/headerCheckAll",
    "nativeConsumer/scalafmtCheckAll",
    "testNativeConsumer"
  ),
  name = Some("Test Native consumer"),
  cond = Some("matrix.project == 'log-effectNative'")
)

lazy val commonSettings = Seq(
  headerEndYear := Some(2026),
  libraryDependencies ++= Seq(
    D.scalacheck.value % Test,
    D.scalatest.value  % Test
  ),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, major)) if major >= 13 => Seq("-Wconf:cat=unused-nowarn:s")
      case _                               => Seq.empty
    }
  }
)

// first versions published for each non-JVM platform, there are no previous artifacts for MiMa
lazy val jsCommonSettings = Seq(
  tlVersionIntroduced := List("2.12", "2.13", "3").map(_ -> "0.19.0").toMap
)
lazy val nativeCommonSettings = Seq(
  tlVersionIntroduced := List("2.12", "2.13", "3").map(_ -> "0.20.0").toMap
)

lazy val `log-effect` = tlCrossRootProject
  .aggregate(core, fs2, zio, interop)
  .settings(
    addCommandAlias("fmt", "scalafmt; Test/scalafmt; scalafmtSbt"),
    addCommandAlias("checkFormat", "scalafmtCheck; Test/scalafmtCheck; scalafmtSbtCheck"),
    addCommandAlias("check", "checkFormat; clean; test; testNativeConsumer"),
    addCommandAlias(
      "testNativeConsumer",
      "nativeConsumer/Compile/nativeLink; nativeConsumer/test"
    )
  )

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .withoutSuffixFor(JVMPlatform)
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "log-effect-core",
    libraryDependencies += D.scribe.value
  )
  .jsSettings(jsCommonSettings)
  .nativeSettings(nativeCommonSettings)
  .jsSettings(
    libraryDependencies ++= Seq(
      D.log4s.value,
      D.`scalajs-java-logging`.value.cross(CrossVersion.for3Use2_13)
    )
  )
  .nativeSettings(
    libraryDependencies += D.`scala-native-java-logging`.value
  )
  .jvmSettings(
    libraryDependencies += D.log4s.value
  )

lazy val fs2 = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .withoutSuffixFor(JVMPlatform)
  .in(file("fs2"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    name := "log-effect-fs2",
    libraryDependencies ++= Seq(
      D.`cats-core`.value,
      D.`cats-effect`.value,
      D.`fs2-core`.value,
      D.scribe.value
    )
  )
  .jsSettings(jsCommonSettings)
  .nativeSettings(nativeCommonSettings)
  .jsSettings(
    libraryDependencies += D.log4s.value
  )
  .jvmSettings(
    libraryDependencies += D.log4s.value
  )

lazy val zio = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .withoutSuffixFor(JVMPlatform)
  .in(file("zio"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    name := "log-effect-zio",
    libraryDependencies ++= Seq(
      D.scribe.value,
      D.zio.value
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      D.log4s.value,
      D.`scala-java-time`.value,
      D.`log4s-testing`.value % Test
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      D.log4s.value,
      D.`log4s-testing`.value % Test
    )
  )
  .jsSettings(jsCommonSettings)
  .nativeSettings(nativeCommonSettings)
  // on Scala 2.12/2.13 linking warns with "Found 7 errors on zio.Cause$CompositeCause..." which
  // are harmless, see https://github.com/scala-native/scala-native/issues/4372
  .nativeSettings(
    libraryDependencies += D.`scala-java-time`.value
  )

lazy val interop = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("interop"))
  .dependsOn(core, fs2)
  .settings(commonSettings)
  .settings(
    name := "log-effect-interop",
    libraryDependencies ++= Seq(
      D.`cats-effect`.value % Test,
      D.`log4cats-core`.value,
      D.`log4cats-testing`.value % Test
    )
  )
  .jsSettings(jsCommonSettings)
  .nativeSettings(nativeCommonSettings)

lazy val nativeConsumer = project
  .in(file("native-consumer"))
  .enablePlugins(ScalaNativePlugin, NoPublishPlugin)
  .dependsOn(fs2.native)
  .settings(commonSettings)
