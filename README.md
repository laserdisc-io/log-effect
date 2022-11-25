# Log Effect
[![Continuous Integration](https://github.com/laserdisc-io/log-effect/actions/workflows/ci.yml/badge.svg)](https://github.com/laserdisc-io/log-effect/actions/workflows/ci.yml)
[![Known Vulnerabilities](https://snyk.io/test/github/laserdisc-io/log-effect/badge.svg?targetFile=build.sbt)](https://snyk.io/test/github/laserdisc-io/log-effect?targetFile=build.sbt)
[![Join the chat at https://gitter.im/laserdisc-io/laserdisc](https://badges.gitter.im/laserdisc-io/laserdisc.svg)](https://gitter.im/laserdisc-io/laserdisc?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

[![log-effect-fs2 Scala version support](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-fs2/latest-by-scala-version.svg?platform=jvm&color=2282c3)](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-fs2)

[![log-effect-zio Scala version support](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-zio/latest-by-scala-version.svg?platform=jvm&color=fb0005)](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-zio)

[![log-effect-core Scala version support](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-core/latest-by-scala-version.svg?platform=jvm&color=9311fc)](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-core)

[![log-effect-interop Scala version support](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-interop/latest-by-scala-version.svg?platform=jvm&color=009933)](https://index.scala-lang.org/laserdisc-io/log-effect/log-effect-interop)

## Start
Log Effect is available for Scala **_2.12_**, **_2.13_** and **_3_**. Helper constructors are provided for **Cats Effect**'s `Sync` `F[_]`, for **Fs2**'s `Stream` and for **ZIO**'s `Task`. 
Add
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-fs2" % <latest-fs2-version>
```
for **Fs2** or **Cats Effect**. Add instead
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-zio" % <latest-zio-version>
```
for **ZIO**. If the intention is, instead, to create your own implementation of the typeclass, adding this
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-core" % <latest-core-version>
```
will be enough. For the latest versions available please refer to the badges below the title.

## Backends
Currently Log Effect supports the following backends
- Log4s
- Java Logging (Jul)
- Scribe
- Console
- No log sink 

## Dependencies

|                          | Cats | Fs2 | Cats Effect | Log Effect Core   |
| ------------------------:| ----:| ---:| -----------:| -----------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.13.svg?label=log-effect-fs2&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.13) | 2.9.0 | 3.4.0 | 3.4.1 | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.13.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.13) |

<br/>

|                          | Zio | Log Effect Core |
| ------------------------:| ---:| ---------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.13.svg?label=log-effect-zio&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.13) | 2.0.4 | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.13) | 

<br/>

|                          | Log4cats | Log Effect Core   |
| ------------------------:| --------:| -----------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-interop_2.13.svg?label=log-effect-interop&colorB=009933)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-interop_2.13) | 2.5.0 | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.13.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.13) |

<br/>

|                          | Log4s  | Scribe |
| ------------------------:| ------:| ------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.13.svg?label=log-effect-core&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.13) | 1.10.0 | 3.10.5 |

<br/>

## Examples

### Get Logs

#### Cats Effect Sync
To get an instance of `LogWriter` for **Cats Effect**'s `Sync` the options below are available (see [here](./fs2/src/main/scala/log/effect/fs2/SyncLogWriter.scala))

*full compiling example [here](./fs2/src/test/scala/ReadmeConstructionCodeSnippetsTest.scala)*
```scala
val log4s1: F[LogWriter[F]] =
  F.delay(l4s.getLogger("test")) map log4sLog[F]

val log4s2: F[LogWriter[F]] = log4sLog("a logger")

val log4s3: F[LogWriter[F]] = {
  case class LoggerClass()
  log4sLog(classOf[LoggerClass])
}

val jul1: F[LogWriter[F]] =
  F.delay(jul.Logger.getLogger("a logger")) map julLog[F]

val jul2: F[LogWriter[F]] = julLog

val scribe1: F[LogWriter[F]] =
  F.delay(scribe.Logger("a logger")) map scribeLog[F]

val scribe2: F[LogWriter[F]] = scribeLog("a logger")

val scribe3: F[LogWriter[F]] = {
  case class LoggerClass()
  scribeLog(classOf[LoggerClass])
}

val console1: LogWriter[F] = consoleLog

val console2: LogWriter[F] = consoleLogUpToLevel(LogLevels.Warn)

val noOp: LogWriter[F] = noOpLog[F]
```

#### Fs2 Stream
Similarly, to get instances of `LogWriter` for **Fs2**'s `Stream` the constructors below are available [here](./fs2/src/main/scala/log/effect/fs2/SyncLogWriter.scala)

*full compiling example [here](./fs2/src/test/scala/ReadmeConstructionCodeSnippetsTest.scala)*
```scala
val log4s1: fs2.Stream[F, LogWriter[F]] =
  Stream.eval(F.delay(l4s.getLogger("test"))) >>= log4sLogStream[F]

val log4s2: fs2.Stream[F, LogWriter[F]] = log4sLogStream("a logger")

val log4s3: fs2.Stream[F, LogWriter[F]] = {
  case class LoggerClass()
  log4sLogStream(classOf[LoggerClass])
}

val jul1: fs2.Stream[F, LogWriter[F]] =
  Stream.eval(F.delay(jul.Logger.getLogger("a logger"))) >>= julLogStream[F]

val jul2: fs2.Stream[F, LogWriter[F]] = julLogStream

val scribe1: fs2.Stream[F, LogWriter[F]] =
  Stream.eval(F.delay(scribe.Logger("a logger"))) >>= scribeLogStream[F]

val scribe2: fs2.Stream[F, LogWriter[F]] = scribeLogStream("a logger")

val scribe3: fs2.Stream[F, LogWriter[F]] = {
  case class LoggerClass()
  scribeLogStream(classOf[LoggerClass])
}

val console1: fs2.Stream[F, LogWriter[F]] = consoleLogStream

val console2: fs2.Stream[F, LogWriter[F]] = consoleLogStreamUpToLevel(LogLevels.Warn)

val noOp: fs2.Stream[F, LogWriter[F]] = noOpLogStream
```
*See [here](https://github.com/laserdisc-io/laserdisc#example-usage) for an example with [Laserdisc](https://github.com/laserdisc-io/laserdisc)*

#### Zio Task
To create instances for `ZIO` some useful constructors can be found [here](./zio/src/main/scala/log/effect/zio/ZioLogWriter.scala). Note as they exploit the power and expressiveness of `RLayer` an the `RIO` pattern as shown below.

##### Create LogWriter as a Layer
*full compiling example [here](./zio/src/test/scala/ReadmeLayerConstructionCodeSnippetsTest.scala)*
```scala
// Case 1: from a possible config
val logNameLiveFromConfig: ULayer[ZLogName] =
  aConfigLive >>> ZLayer(ZIO.service[AConfig].map(c => LogName(c.logName)))

val log4sCase1: TaskLayer[ZLogWriter] =
  logNameLiveFromConfig >>> log4sLayerFromName

val scribeCase1: TaskLayer[ZLogWriter] =
  logNameLiveFromConfig >>> scribeLayerFromName

// Case 2: from a name
val log4sCase2: TaskLayer[ZLogWriter] =
  logNameLive >>> log4sLayerFromName

val scribeCase2: TaskLayer[ZLogWriter] =
  logNameLive >>> scribeLayerFromName

// Case 3: from a logger
val log4sCase3: TaskLayer[ZLogWriter] =
  log4sLoggerLive >>> log4sLayerFromLogger

val julCase3: TaskLayer[ZLogWriter] =
  julLoggerLive >>> julLayerFromLogger

val scribeCase3: TaskLayer[ZLogWriter] =
  scribeLoggerLive >>> scribeLayerFromLogger
```

##### Create LogWriter as RIO
*full compiling example [here](./zio/src/test/scala/ReadmeRioConstructionCodeSnippetsTest.scala)*
```scala
// Case 1: from a possible config in a Layer (gives a Layer)
val log4sCase1: RLayer[AConfig, ZLogWriter] =
  ZLayer(ZIO.serviceWithZIO { c =>
    log4sFromName.provideEnvironment(ZEnvironment(c.logName))
  })
val scribe4sCase1: RLayer[AConfig, ZLogWriter] =
  ZLayer(ZIO.serviceWithZIO { c =>
    scribeFromName.provideEnvironment(ZEnvironment(c.logName))
  })

// Case 2: from a name
val log4sCase2: Task[Unit] =
  log4sFromName.provideEnvironment(ZEnvironment(aLogName)).flatMap { logger =>
    someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
  }
val scribeCase2: Task[Unit] =
  scribeFromName.provideEnvironment(ZEnvironment(aLogName)).flatMap { logger =>
    someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
  }

// Case 3: from a logger
val log4sCase3: Task[Unit] =
  for {
    logger    <- ZIO.attempt(l4s.getLogger(aLogName))
    logWriter <- log4sFromLogger.provideEnvironment(ZEnvironment(logger))
    _         <- someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logWriter))
  } yield ()
val julCase3: Task[Unit] =
  for {
    logger    <- ZIO.attempt(jul.Logger.getLogger(aLogName))
    logWriter <- julFromLogger.provideEnvironment(ZEnvironment(logger))
    _         <- someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logWriter))
  } yield ()
val scribeCase3: Task[Unit] =
  for {
    logger    <- ZIO.attempt(scribe.Logger(aLogName))
    logWriter <- scribeFromLogger.provideEnvironment(ZEnvironment(logger))
    _         <- someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logWriter))
  } yield ()

// Case 4: from a class
val log4sCase4: Task[Unit] = {
  case class LoggerClass();
  log4sFromClass.provideEnvironment(ZEnvironment(classOf[LoggerClass])).flatMap { logger =>
    someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
  }
}
val scribeCase4: Task[Unit] = {
  case class LoggerClass();
  scribeFromClass.provideEnvironment(ZEnvironment(classOf[LoggerClass])).flatMap { logger =>
    someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
  }
}

// Case 5 (Jul): from global logger object
val julCase5: Task[Unit] =
  julGlobal.flatMap(logger =>
    someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
  )

// Case 6: console logger
val console1: Task[Unit] =
  someZioProgramUsingLogs.provideEnvironment(ZEnvironment(consoleLog))

val console2: Task[Unit] =
  someZioProgramUsingLogs.provideEnvironment(
    ZEnvironment(consoleLogUpToLevel(LogLevels.Warn))
  )

// Case 7: No-op logger
val noOp: Task[Unit] =
  someZioProgramUsingLogs.provideEnvironment(ZEnvironment(noOpLog))
```


### Submit Logs

The following ways of submitting logs are supported:

- _in a monadic sequence of effects_
```scala
import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import log.effect.LogWriter

def process[F[_]](implicit F: Sync[F], log: LogWriter[F]): F[(Int, Int)] =
  for {
    _ <- log.trace("We start")
    a <- F.delay(10)
    _ <- log.trace("Keep going")
    b <- F.delay(20)
    _ <- log.trace("We reached this point")
    _ <- log.info(s"Process complete: ${(a, b)}")
  } yield (a, b)
```

 - _in a streaming environment using `LogWriter`'s syntax_
```scala
import java.nio.channels.AsynchronousChannelGroup

import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import cats.syntax.flatMap._
import log.effect.LogWriter

import scala.concurrent.ExecutionContext

sealed trait RedisClient[F[_]] {
  def address: String
}
object RedisClient {
  def apply[F[_]](addr: String): fs2.Stream[F, RedisClient[F]] =
    fs2.Stream.emit(new RedisClient[F] { val address = addr })
}

implicit def EC: ExecutionContext         = ???
implicit def CG: AsynchronousChannelGroup = ???

def redisClient[F[_]: ConcurrentEffect: ContextShift: Timer](
  address: String
)(implicit log: LogWriter[F]): fs2.Stream[F, RedisClient[F]] =
  RedisClient[F](address) evalMap { client =>
    log.info(s"Laserdisc Redis client for $address") >> ConcurrentEffect[F].pure(client)
  }
```

- _in a streaming environment using `fs2` streams' syntax_
```scala
import java.nio.channels.AsynchronousChannelGroup

import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import log.effect.LogWriter
import log.effect.fs2.syntax._

import scala.concurrent.ExecutionContext

sealed trait RedisClient[F[_]] {
  def address: String
}
object RedisClient {
  def apply[F[_]](addr: String): fs2.Stream[F, RedisClient[F]] =
    fs2.Stream.emit(new RedisClient[F] { val address = addr })
}

implicit def EC: ExecutionContext         = ???
implicit def CG: AsynchronousChannelGroup = ???

def redisCache[F[_]: ConcurrentEffect: ContextShift: Timer](
  address: String
)(implicit log: LogWriter[F]): fs2.Stream[F, RedisClient[F]] =
  for {
    _      <- log.infoS(s"About to connect a Laserdisc Redis client for $address")
    client <- RedisClient[F](address)
    _      <- log.infoS("The connection went fine. Talking to the server")
  } yield client
```

- _through the companion's syntax_
```scala
import cats.effect.Sync
import cats.syntax.apply._
import cats.syntax.flatMap._
import log.effect.LogWriter

def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] =
  source evalMap { n =>
    LogWriter.debug("Processing a number") >>
      LogWriter.debug(n.toString) >>
      Sync[F].pure(n * 2) <*
      LogWriter.debug("Processed")
  } handleErrorWith { th =>
    fs2.Stream eval (
      LogWriter.error("Ops, something didn't work", th) >> Sync[F].pure(0)
    )
  }
```

- _through the companion's syntax for `fs2` streams_
```scala
import cats.effect.Sync
import cats.syntax.apply._
import cats.syntax.flatMap._
import log.effect.LogWriter
import log.effect.fs2.syntax._

def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] =
  (source >>= { n =>
    LogWriter.debugS("Processing a number") >>
      LogWriter.debugS(n.toString) >>
      fs2.Stream.eval(Sync[F].pure(n * 2)) <*
      LogWriter.debugS("Processed")
  }) handleErrorWith { th =>
    LogWriter.errorS("Ops, something didn't work", th) >> fs2.Stream.eval(Sync[F].pure(0))
  }
```

- _through the companion's accessor for the `write` method_
```scala
import java.nio.channels.AsynchronousChannelGroup

import cats.Show
import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import cats.instances.string._
import cats.syntax.either._
import cats.syntax.flatMap._
import log.effect.LogLevels.{ Debug, Error }
import log.effect.fs2.interop.show._
import log.effect.{ Failure, LogWriter }

import scala.concurrent.ExecutionContext

sealed trait RedisClient[F[_]] {
  def address: String
}
object RedisClient {
  def apply[F[_]](addr: String): fs2.Stream[F, RedisClient[F]] =
    fs2.Stream.emit(new RedisClient[F] { val address = addr })
}

type |[A, B] = Either[A, B]

implicit def EC: ExecutionContext         = ???
implicit def CG: AsynchronousChannelGroup = ???

def redisClient[F[_]: ConcurrentEffect: ContextShift: Timer: LogWriter](
  address: String
): fs2.Stream[F, Throwable | RedisClient[F]] = {
  // Cats Show instances are needed for every logged type
  implicit val clientShow: Show[RedisClient[F]] = ???

  RedisClient[F](address) evalMap { client =>
    LogWriter.write(Debug, "Connected client details:") >> // Or
      LogWriter.debug(address) >>                          // And
      LogWriter.debug(client) >>
      ConcurrentEffect[F].pure(client.asRight)
  } handleErrorWith { th =>
    fs2.Stream eval (
      LogWriter.write(
        Error,
        Failure("Ops, something didn't work", th)
      ) >> ConcurrentEffect[F].pure(th.asLeft)
    )
  }
}
```
or using the fs2 Stream specific syntax like `writeS` or the level alternatives for types that provide a `cats.Show` instance 
```scala
import cats.Show
import cats.effect.Sync
import log.effect.LogLevels.Error
import log.effect.{ Failure, LogWriter }
import log.effect.fs2.syntax._

trait A
object A {
  def empty: A = ???
  implicit val aShow: Show[A] = new Show[A] {
    override def show(t: A): String = ???
  }
}

def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, A] = {
  // Cats Show instances are needed for every logged type
  implicit def intShow: Show[Int] = ???

  def processAnInt: Int => A = ???

  (for {
    n <- source
    _ <- LogWriter.debugS("Processing a number")
    _ <- LogWriter.debugS(n) // N.B. the syntax requires a `cats.Show` for `Int`
    r <- (processAnInt andThen fs2.Stream.emit)(n)
    _ <- LogWriter.debugS("Processed")
    _ <- LogWriter.debugS(r) // Same here, a `cats.Show` for `A` is needed
  } yield r) handleErrorWith { th =>
    LogWriter.writeS(
      Error,
      Failure("Ops, something didn't work", th)
    ) >> fs2.Stream.emit(A.empty) // and `write again`
  }
}
```
**NB:** note above the `LogWriter`'s implicit evidence given as context bound and the `import log.effect.fs2.interop.show._`. The latter is needed to summon an `internal.Show` given a `cats.Show`.

## Interop

In some cases log-effect can be used in projects that have a different logging system already in place. A basic interoperability with other logging libraries is provided in `log-effect-interop`. The supported ones at the moment are
- log4cats

See an example below. 
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-interop" % <latest-interop-version>
```
```scala
import cats.effect.{Resource, Sync}
import cats.syntax.flatMap._
import io.chrisdavenport.log4cats.Logger
import log.effect.LogWriter

import log.effect.interop.log4cats._

sealed trait RedisClient[F[_]] {
  def address: String
  def write: F[Unit]
}
object RedisClient {
  def apply[F[_]: LogWriter](addr: String)(implicit F: Sync[F]): Resource[F, RedisClient[F]] =
    Resource.make(
      F.pure(
        new RedisClient[F] {
          val address        = addr
          def write: F[Unit] = LogWriter.info(address)
        }
      )
    )(_ => F.unit)
}

def buildLog4catsLogger[F[_]]: F[Logger[F]] = ???

def storeOwnAddress[F[_]: Sync](address: String): F[Unit] =
  buildLog4catsLogger[F] >>= { implicit l =>
    RedisClient[F](address).use { cl =>
      cl.write
    }
  }
```
**NB:** note the `import log.effect.interop.log4cats._` that enables the derivation of a `LogWriter[F]` when a `log4cats`' `Logger[F]` is in scope.

## License

Log-effect is licensed under the **[MIT License](LICENSE)** (the "License"); you may not use this software except in
compliance with the License. <br>Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
