import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val scala212Options = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-explaintypes",
  "-Yrangepos",
  "-feature",
  "-Xfuture",
  "-Ypartial-unification",
  "-language:higherKinds",
  "-language:existentials",
  "-unchecked",
  "-Yno-adapted-args",
  "-Xlint:_,-type-parameter-shadow",
  "-Xsource:2.13",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfatal-warnings",
  "-opt:l:inline",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:_,imports",
  "-opt-warnings",
  "-Xlint:constant",
  "-Ywarn-extra-implicit",
  "-opt-inline-from:<source>"
)

lazy val scala213Options = scala212Options diff Seq(
  "-Ywarn-nullary-override",
  "-Ypartial-unification",
  "-Ywarn-nullary-unit",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Yno-adapted-args",
  "-Xfuture"
)

/**
  * Dependencies
  */
lazy val versionOf = new {
  val cats          = "2.1.1"
  val catsEffect    = "2.1.3"
  val fs2           = "2.3.0"
  val kindProjector = "0.11.0"
  val log4s         = "1.8.2"
  val scalaCheck    = "1.14.3"
  val scalaTest     = "3.2.0-M4"
  val zio           = "1.0.0-RC19"
  val scribe        = "2.7.12"
  val silencer      = "1.7.0"
}

lazy val coreDependencies = Seq(
  "org.log4s" %% "log4s"  % versionOf.log4s,
  "com.outr"  %% "scribe" % versionOf.scribe
) map (_.withSources)

lazy val fs2Dependencies = Seq(
  "org.log4s"     %% "log4s"       % versionOf.log4s,
  "com.outr"      %% "scribe"      % versionOf.scribe,
  "org.typelevel" %% "cats-core"   % versionOf.cats,
  "org.typelevel" %% "cats-effect" % versionOf.catsEffect,
  "co.fs2"        %% "fs2-core"    % versionOf.fs2
) map (_.withSources)

lazy val zioDependencies = Seq(
  "org.log4s" %% "log4s"  % versionOf.log4s,
  "com.outr"  %% "scribe" % versionOf.scribe,
  "dev.zio"   %% "zio"    % versionOf.zio
) map (_.withSources)

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

/**
  * Settings
  */
lazy val crossBuildSettings = Seq(
  libraryDependencies ++= testDependencies ++ compilerPluginsDependencies,
  organization := "io.laserdisc",
  parallelExecution in Test := false,
  scalacOptions ++=
    (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => scala212Options
      case _             => scala213Options
    }),
  parallelExecution in Test := false
)

lazy val format = Command.command("format") { state =>
  "scalafmt" :: "test:scalafmt" :: "scalafmtSbt" :: state
}

lazy val checkFormat = Command.command("checkFormat") { state =>
  "scalafmtCheck" :: "test:scalafmtCheck" :: "scalafmtSbtCheck" :: state
}

lazy val fullCiBuild = Command.command("fullCiBuild") { state =>
  "checkFormat" :: "clean" :: "test" :: state
}

lazy val customCommands: Seq[Def.Setting[_]] = Seq(
  commands ++= Seq(format, checkFormat, fullCiBuild)
)

val scalafmtCheckStep = ReleaseStep(
  action = st => {
    val extracted = Project.extract(st)
    val ref       = extracted.get(thisProjectRef)
    extracted.runAggregated(scalafmtCheck in Compile in ref, st)
  },
  enableCrossBuild = false
)

val scalafmtCheckTestStep = ReleaseStep(
  action = st => {
    val extracted = Project.extract(st)
    val ref       = extracted.get(thisProjectRef)
    extracted.runAggregated(scalafmtCheck in Test in ref, st)
  },
  enableCrossBuild = false
)

val scalafmtCheckSbtStep = ReleaseStep(
  action = st => {
    val extracted = Project.extract(st)
    val ref       = extracted.get(thisProjectRef)
    extracted.runAggregated(scalafmtSbtCheck in Test in ref, st)
  },
  enableCrossBuild = false
)

lazy val releaseSettings: Seq[Def.Setting[_]] = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    runClean,
    scalafmtCheckStep,
    scalafmtCheckTestStep,
    scalafmtCheckSbtStep,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    releaseStepCommand("sonatypeRelease"),
    commitNextVersion,
    pushChanges
  ),
  releaseCrossBuild := true,
  publishMavenStyle := true,
  credentials := Credentials(Path.userHome / ".sbt" / "sonatype_credentials") :: Nil,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  licenses := Seq(
    "MIT License" ->
      url("https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE")
  ),
  homepage := Some(url("http://laserdisc.io")),
  publishTo := sonatypePublishTo.value,
  pomExtra :=
    <scm>
      <url>https://github.com/laserdisc-io/log-effect/tree/master</url>
      <connection>scm:git:git@github.com:laserdisc-io/log-effect.git</connection>
    </scm>
    <developers>
      <developer>
        <id>barambani</id>
        <name>Filippo Mariotti</name>
        <url>https://github.com/barambani</url>
      </developer>
    </developers>
)

lazy val root = project
  .in(file("."))
  .aggregate(core, fs2, zio)
  .settings(crossBuildSettings)
  .settings(customCommands)
  .settings(releaseSettings)
  .settings(
    name := "log-effect",
    publishArtifact := false
  )

lazy val core = project
  .in(file("core"))
  .settings(crossBuildSettings)
  .settings(customCommands)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-core",
    libraryDependencies ++= coreDependencies
  )

lazy val fs2 = project
  .in(file("fs2"))
  .dependsOn(core)
  .settings(crossBuildSettings)
  .settings(customCommands)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-fs2",
    libraryDependencies ++= fs2Dependencies
  )

lazy val zio = project
  .in(file("zio"))
  .dependsOn(core)
  .settings(crossBuildSettings)
  .settings(customCommands)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-zio",
    libraryDependencies ++= zioDependencies
  )
