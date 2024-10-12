val scala_212 = "2.12.20"
val scala_213 = "2.13.14"
val scala_3   = "3.3.3"

val V = new {
  val cats       = "2.12.0"
  val catsEffect = "3.5.4"
  val fs2        = "3.11.0"
  val log4cats   = "2.7.0"
  val log4s      = "1.10.0"
  val scalaCheck = "1.18.1"
  val scalaTest  = "3.2.19"
  val scribe     = "3.15.0"
  val zio        = "2.1.11"
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
  lazy val scalatest          = Def.setting("org.scalatest" %%% "scalatest" % V.scalaTest)
  lazy val scribe             = Def.setting("com.outr" %%% "scribe" % V.scribe)
  lazy val zio                = Def.setting("dev.zio" %%% "zio" % V.zio)
}

ThisBuild / tlBaseVersion           := "0.19"
ThisBuild / tlCiReleaseBranches     := Seq("master")
ThisBuild / tlVersionIntroduced     := Map("3" -> "0.16.3")
ThisBuild / tlSonatypeUseLegacyHost := true
ThisBuild / organization            := "io.laserdisc"
ThisBuild / organizationName        := "LaserDisc"
ThisBuild / licenses                := Seq(License.MIT)
ThisBuild / startYear               := Some(2018)
ThisBuild / developers              := List(tlGitHubDev("barambani", "Filippo Mariotti"))
ThisBuild / crossScalaVersions      := Seq(scala_212, scala_213, scala_3)
ThisBuild / scalaVersion            := scala_213
ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17")
)
ThisBuild / githubWorkflowBuildMatrixExclusions := Seq()
ThisBuild / Test / parallelExecution            := false

ThisBuild / libraryDependencies ++= Seq(
  D.scalacheck.value % Test,
  D.scalatest.value  % Test
)

lazy val commonSettings = Seq(
  headerEndYear := Some(2024)
)

lazy val root = tlCrossRootProject
  .aggregate(core, fs2, zio, interop)
  .settings(commonSettings)
  .settings(
    addCommandAlias("fmt", "scalafmt; Test/scalafmt; scalafmtSbt"),
    addCommandAlias("checkFormat", "scalafmtCheck; Test/scalafmtCheck; scalafmtSbtCheck"),
    addCommandAlias("check", "checkFormat; clean; test")
  )

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "log-effect-core",
    libraryDependencies ++= Seq(D.log4s.value, D.scribe.value)
  )

lazy val fs2 = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("fs2"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    name := "log-effect-fs2",
    libraryDependencies ++= Seq(
      D.`cats-core`.value,
      D.`cats-effect`.value,
      D.`fs2-core`.value,
      D.log4s.value,
      D.scribe.value
    )
  )

lazy val zio = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("zio"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    name := "log-effect-zio",
    libraryDependencies ++= Seq(
      D.log4s.value,
      D.`log4s-testing`.value % Test,
      D.scribe.value,
      D.zio.value
    )
  )

lazy val interop = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
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
