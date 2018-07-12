lazy val `scala 211` = "2.11.12"
lazy val `scala 212` = "2.12.6"

/**
  * Scalac options
  */
lazy val crossBuildOptions = Seq(
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
  "-Xfatal-warnings"
)

lazy val scala212Options = Seq(
  "-opt:l:inline",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:_,imports",
  "-opt-warnings",
  "-Xlint:constant",
  "-Ywarn-extra-implicit",
  "-opt-inline-from:<source>"
)

/**
  * Dependencies
  */
lazy val versionOf = new {
  val cats = "1.1.0"
  val catsEffect = "0.10.1"
  val log4s = "1.6.1"
  val fs2 = "0.10.5"
  val scalaCheck = "1.14.0"
  val scalaTest = "3.0.5"
  val kindProjector = "0.9.7"
  val silencer = "1.0"
}

lazy val sharedDependencies = Seq(
  "com.github.ghik" %% "silencer-lib" % versionOf.silencer % Provided
) map (_.withSources)

lazy val coreDependencies = Seq(
  "org.typelevel" %% "cats-core" % versionOf.cats,
  "org.typelevel" %% "cats-effect" % versionOf.catsEffect,
  "org.log4s" %% "log4s" % versionOf.log4s,
) map (_.withSources)

lazy val fs2Dependencies = Seq(
  "co.fs2" %% "fs2-core" % versionOf.fs2
) map (_.withSources)

lazy val testDependencies = Seq(
  "org.scalacheck" %% "scalacheck" % versionOf.scalaCheck % Test,
  "org.scalatest" %% "scalatest" % versionOf.scalaTest % Test
)

lazy val compilerPluginsDependencies = Seq(
  compilerPlugin(
    "org.spire-math" %% "kind-projector" % versionOf.kindProjector cross CrossVersion.binary),
  compilerPlugin("com.github.ghik" %% "silencer-plugin" % versionOf.silencer),
)

/**
  * Settings
  */
lazy val crossBuildSettings = Seq(
  scalaVersion := `scala 212`,
  crossScalaVersions := Seq(`scala 211`, `scala 212`),
  scalacOptions ++= crossBuildOptions,
  libraryDependencies ++= sharedDependencies ++ testDependencies ++ compilerPluginsDependencies,
  organization := "io.laserdisc",
  parallelExecution in Test := false,
  scalacOptions ++=
    (scalaVersion.value match {
      case `scala 212` => scala212Options
      case _           => Seq()
    })
)

lazy val releaseSettings: Seq[Def.Setting[_]] = Seq(
  releaseCrossBuild := true,
  publishMavenStyle := true,
  credentials := Credentials(Path.userHome / ".ivy2" / ".credentials") :: Nil,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
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
  .aggregate(core, fs2)
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
    addCommandAlias("ciFullBuild", ";checkFormat;clean;test"),
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
