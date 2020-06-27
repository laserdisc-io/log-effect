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
  val fs2           = "2.4.2"
  val kindProjector = "0.11.0"
  val log4cats      = "1.1.1"
  val log4s         = "1.8.2"
  val scalaCheck    = "1.14.3"
  val scalaTest     = "3.2.0"
  val zio           = "1.0.0-RC21-1"
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

lazy val interopDependencies = Seq(
  "io.chrisdavenport" %% "log4cats-core"  % versionOf.log4cats,
  "io.chrisdavenport" %% "log4cats-slf4j" % versionOf.log4cats   % Test,
  "org.typelevel"     %% "cats-effect"    % versionOf.catsEffect % Test
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
  parallelExecution in Test := false,
  scalacOptions ++=
    (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => scala212Options
      case _             => scala213Options
    })
)

lazy val releaseSettings: Seq[Def.Setting[_]] = Seq(
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  organization := "io.laserdisc",
  homepage := Some(url("http://laserdisc.io")),
  developers := List(
    Developer("barambani", "Filippo Mariotti", "", url("https://github.com/barambani"))
  ),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/laserdisc-io/log-effect/tree/master"),
      "scm:git:git@github.com:laserdisc-io/log-effect.git",
      "scm:git:git@github.com:laserdisc-io/log-effect.git"
    )
  ),
  licenses := Seq(
    "MIT License" ->
      url("https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE")
  )
)

lazy val root = project
  .in(file("."))
  .aggregate(core, fs2, zio, interop)
  .settings(crossBuildSettings)
  .settings(releaseSettings)
  .settings(
    name := "log-effect",
    publishArtifact := false,
    addCommandAlias("format", ";scalafmt;test:scalafmt;scalafmtSbt"),
    addCommandAlias(
      "checkFormat",
      ";scalafmtCheck;test:scalafmtCheck;scalafmtSbtCheck"
    ),
    addCommandAlias(
      "ciBuild",
      ";clean;test"
    ),
    addCommandAlias(
      "fullBuild",
      ";checkFormat;ciBuild"
    ),
    // travis release aliases
    addCommandAlias(
      "setReleaseOptions",
      "set scalacOptions ++= Seq(\"-opt:l:method\", \"-opt:l:inline\", \"-opt-inline-from:laserdisc.**\", \"-opt-inline-from:<sources>\")"
    ),
    addCommandAlias("releaseIt", ";clean;setReleaseOptions;session list;compile;ci-release")
  )

lazy val core = project
  .in(file("core"))
  .settings(crossBuildSettings)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-core",
    libraryDependencies ++= coreDependencies
  )

lazy val fs2 = project
  .in(file("fs2"))
  .dependsOn(core)
  .settings(crossBuildSettings)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-fs2",
    libraryDependencies ++= fs2Dependencies
  )

lazy val zio = project
  .in(file("zio"))
  .dependsOn(core)
  .settings(crossBuildSettings)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-zio",
    libraryDependencies ++= zioDependencies
  )

lazy val interop = project
  .in(file("interop"))
  .dependsOn(core, fs2)
  .settings(crossBuildSettings)
  .settings(releaseSettings)
  .settings(
    name := "log-effect-interop",
    libraryDependencies ++= interopDependencies
  )
