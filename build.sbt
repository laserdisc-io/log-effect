lazy val typelevelOrganization = "org.typelevel"
lazy val globalOrganization = scalaOrganization in Global

lazy val `typelevel scala 212` = "2.12.4-bin-typelevel-4"
lazy val `scala 211` = "2.11.12"
lazy val `scala 212` = "2.12.6"


/**
  * Scalac options
  */
lazy val crossBuildOptions = Seq (
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

lazy val scala212Options = Seq (
  "-opt:l:inline",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:_,imports",
  "-opt-warnings",
  "-Xlint:constant",
  "-Ywarn-extra-implicit",
  "-opt-inline-from:<source>"
)

lazy val typeLevelScalaOptions = Seq(
  "-Yinduction-heuristics",       // speeds up the compilation of inductive implicit resolution
  "-Ykind-polymorphism",          // type and method definitions with type parameters of arbitrary kinds
  "-Yliteral-types",              // literals can appear in type position
  "-Xstrict-patmat-analysis",     // more accurate reporting of failures of match exhaustivity
  "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
)

lazy val testOnlyOptions = Seq(
  "-P:splain:implicits:true",
  "-P:splain:tree:true"
)


/**
  * Dependencies
  */
lazy val versionOf = new {
  val cats          = "1.1.0"
  val catsEffect    = "1.0.0-RC2"
  val scalaCheck    = "1.14.0"
  val kindProjector = "0.9.7"
  val log4s         = "1.6.1"
  val silencer      = "1.0"
  val fs2           = "1.0.0-M1"
}

lazy val sharedDependencies = Seq(
  "com.github.ghik" %% "silencer-lib" % versionOf.silencer % Provided
) map (_.withSources)

lazy val coreDependencies = Seq(
  "org.typelevel"   %% "cats-core"    % versionOf.cats,
  "org.typelevel"   %% "cats-effect"  % versionOf.catsEffect,
  "org.log4s"       %% "log4s"        % versionOf.log4s,
) map (_.withSources)

lazy val fs2Dependencies = Seq(
  "co.fs2" %% "fs2-core" % versionOf.fs2
) map (_.withSources)

lazy val testDependencies = Seq(
  "org.scalacheck" %% "scalacheck" % versionOf.scalaCheck % Test
)

lazy val compilerPluginsDependencies = Seq(
  compilerPlugin("org.spire-math"   %% "kind-projector"   % versionOf.kindProjector cross CrossVersion.binary),
  compilerPlugin("com.github.ghik"  %% "silencer-plugin"  % versionOf.silencer),
)


/**
  * Settings
  */
lazy val crossBuildSettings = Seq(
  scalacOptions           ++= crossBuildOptions,
  crossScalaVersions 	    :=  Seq(`scala 211`, `scala 212`),
  scalaOrganization :=
    (scalaVersion.value match {
      case `typelevel scala 212`  => typelevelOrganization
      case _                      => globalOrganization.value
    }),
  scalacOptions ++=
    (scalaVersion.value match {
      case `scala 212`            => scala212Options
      case `typelevel scala 212`  => scala212Options ++ typeLevelScalaOptions
      case _                      => Seq()
    }),
  libraryDependencies     ++= sharedDependencies ++ testDependencies ++ compilerPluginsDependencies,
  scalacOptions in Test   ++= testOnlyOptions,
  scalaVersion            :=  `typelevel scala 212`,
  organization            :=  "io.laserdisc"
)

lazy val releaseSettings: Seq[Def.Setting[_]] = Seq(
  releaseCrossBuild             := true,
  publishMavenStyle             := true,
  credentials                   := Credentials(Path.userHome / ".ivy2" / ".credentials") :: Nil,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishArtifact in Test       := false,
  pomIncludeRepository          := { _ => false },
  licenses                      := Seq("MIT License" -> url("https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE")),
  homepage                      := Some(url("http://laserdisc.io")),
  publishTo                     := sonatypePublishTo.value,
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
    publishArtifact := false
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
    name :=  "log-effect-fs2",
    libraryDependencies ++= fs2Dependencies
  )