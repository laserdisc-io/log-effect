val scala_212 = "2.12.15"
val scala_213 = "2.13.8"

val versionOf = new {
  val cats          = "2.7.0"
  val catsEffect    = "3.3.7"
  val fs2           = "3.2.5"
  val kindProjector = "0.13.2"
  val log4cats      = "2.2.0"
  val log4s         = "1.10.0"
  val scalaCheck    = "1.15.4"
  val scalaTest     = "3.2.11"
  val zio           = "1.0.13"
  val scribe        = "3.8.2"
  val silencer      = "1.7.8"
}

lazy val coreDependencies = Seq(
  "org.log4s" %% "log4s"  % versionOf.log4s,
  "com.outr"  %% "scribe" % versionOf.scribe
).map(_.withSources)

lazy val fs2Dependencies = Seq(
  "org.log4s"     %% "log4s"       % versionOf.log4s,
  "com.outr"      %% "scribe"      % versionOf.scribe,
  "org.typelevel" %% "cats-core"   % versionOf.cats,
  "org.typelevel" %% "cats-effect" % versionOf.catsEffect,
  "co.fs2"        %% "fs2-core"    % versionOf.fs2
).map(_.withSources)

lazy val zioDependencies = Seq(
  "org.log4s" %% "log4s"  % versionOf.log4s,
  "com.outr"  %% "scribe" % versionOf.scribe,
  "dev.zio"   %% "zio"    % versionOf.zio
).map(_.withSources)

lazy val interopDependencies = Seq(
  "org.typelevel" %% "log4cats-core"  % versionOf.log4cats,
  "org.typelevel" %% "log4cats-slf4j" % versionOf.log4cats   % Test,
  "org.typelevel" %% "cats-effect"    % versionOf.catsEffect % Test
).map(_.withSources)

lazy val testDependencies = Seq(
  "org.scalacheck" %% "scalacheck"    % versionOf.scalaCheck % Test,
  "org.scalatest"  %% "scalatest"     % versionOf.scalaTest  % Test,
  "org.log4s"      %% "log4s-testing" % versionOf.log4s      % Test
)

lazy val compilerPluginsDependencies = Seq(
  compilerPlugin(
    "org.typelevel" %% "kind-projector" % versionOf.kindProjector cross CrossVersion.full
  ),
  compilerPlugin(
    "com.github.ghik" %% "silencer-plugin" % versionOf.silencer cross CrossVersion.full
  ),
  "com.github.ghik" %% "silencer-lib" % versionOf.silencer % Provided cross CrossVersion.full
)

ThisBuild / tlBaseVersion       := "0.16"
ThisBuild / tlCiReleaseBranches := Seq("master")
ThisBuild / organization        := "io.laserdisc"
ThisBuild / organizationName    := "LaserDisc"
ThisBuild / licenses            := Seq(License.MIT)
ThisBuild / developers          := List(tlGitHubDev("barambani", "Filippo Mariotti"))
ThisBuild / crossScalaVersions  := Seq(scala_212, scala_213)
ThisBuild / scalaVersion        := scala_213
ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("8"),
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17")
)
ThisBuild / Test / parallelExecution := false

ThisBuild / libraryDependencies ++= testDependencies ++ compilerPluginsDependencies

lazy val root = tlCrossRootProject.aggregate(core, fs2, zio, interop)

lazy val core = project
  .in(file("core"))
  .settings(
    name := "log-effect-core",
    libraryDependencies ++= coreDependencies
  )

lazy val fs2 = project
  .in(file("fs2"))
  .dependsOn(core)
  .settings(
    name := "log-effect-fs2",
    libraryDependencies ++= fs2Dependencies
  )

lazy val zio = project
  .in(file("zio"))
  .dependsOn(core)
  .settings(
    name := "log-effect-zio",
    libraryDependencies ++= zioDependencies
  )

lazy val interop = project
  .in(file("interop"))
  .dependsOn(core, fs2)
  .settings(
    name := "log-effect-interop",
    libraryDependencies ++= interopDependencies
  )
