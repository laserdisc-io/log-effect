# Log Effect
[![Build Status](https://travis-ci.org/laserdisc-io/log-effect.svg?branch=master)](https://travis-ci.org/laserdisc-io/log-effect)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f3d016e4069a445b866ec4a059b23d1c)](https://app.codacy.com/app/barambani/log-effect?utm_source=github.com&utm_medium=referral&utm_content=laserdisc-io/log-effect&utm_campaign=Badge_Grade_Dashboard)
[![Known Vulnerabilities](https://snyk.io/test/github/laserdisc-io/log-effect/badge.svg?targetFile=build.sbt)](https://snyk.io/test/github/laserdisc-io/log-effect?targetFile=build.sbt)
[![scaladex](https://img.shields.io/badge/scaladex-log--effect-orange.svg)](https://index.scala-lang.org/laserdisc-io/log-effect)
[![Join the chat at https://gitter.im/laserdisc-io/laserdisc](https://badges.gitter.im/laserdisc-io/laserdisc.svg)](https://gitter.im/laserdisc-io/laserdisc?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=core%202.12&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.12.svg?label=fs2%202.12&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.12)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.12.svg?label=zio%202.12&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.12)

[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.13.svg?label=core%202.13&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.13)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.13.svg?label=fs2%202.13&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.13)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.13.svg?label=zio%202.13&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.13)


## Start
Log Effect is available for Scala **_2.12_** and **_2.13_**. Helper constructors are provided for **Cats Effect**'s `Sync` `F[_]`, for **Fs2**'s `Stream` and for **ZIO**'s `Task`. 
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

|                          | Cats | Fs2 | Cats Effect | Log Effect Core   |
| ------------------------:| ----:| ---:| -----------:| -----------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.12.svg?label=log-effect-fs2&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.12) | 2.1.1 | 2.3.0 | 2.1.2 | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12) |
| v0.8.0  | 1.6.1 | 1.0.5    | 1.3.1  | v0.8.0  |
| v0.3.5  | 1.4.0 | 1.0.0-M5 | 1.0.0  | v0.3.5  |
| v0.2.2  | 1.2.0 | 0.10.5   | 0.10.1 | v0.2.2  |
| v0.1.14 | 1.2.0 | 0.10.5   |        | v0.1.14 |

<br>

|                          | Zio | Scalaz ZIO | Log Effect Core   |
| ------------------------:| ---:| ----------:| -----------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.12.svg?label=log-effect-zio&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.12) | 1.0.0-RC18-2 |  | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12) |
| v0.8.0 | 1.0.0-RC9 |  | v0.8.0 |
| v0.7.0 |  | 1.0-RC4 | v0.7.0  |
| v0.3.5 |  | 0.2.7   | v0.3.5  | 

<br>

|                          | Cats  | Cats Effect | Log4s  | Scribe |
| ------------------------:| -----:| -----------:| ------:| ------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=log-effect-core&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12) |  |  | 1.8.2 | 2.7.12 |
| v0.8.0  |       |             | 1.8.2  | 2.7.8  |
| v0.3.5  |       |             | 1.6.1  |        |
| v0.2.2  | 1.2.0 |             | 1.6.1  |        |
| v0.1.14 | 1.2.0 | 0.10.1      | 1.6.1  |        |

<br>

## Examples

### Get Logs

#### Cats Effect Sync
To get an instance of `LogWriter` for **Cats Effect**'s `Sync` the options below are available
```scala
import java.util.{ logging => jul }

import cats.effect.Sync
import cats.syntax.functor._
import log.effect.fs2.SyncLogWriter._
import log.effect.{ LogLevels, LogWriter }
import org.{ log4s => l4s }

sealed abstract class App[F[_]](implicit F: Sync[F]) {
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
}
```

#### Fs2 Stream
Simirarly, to get instances of `LogWriter` for **Fs2**'s `Stream` the constructors below are available
```scala
import java.util.{ logging => jul }

import cats.effect.Sync
import cats.syntax.flatMap._
import fs2.Stream
import log.effect.fs2.Fs2LogWriter._
import log.effect.{ LogLevels, LogWriter }
import org.{ log4s => l4s }

sealed abstract class App[F[_]](implicit F: Sync[F]) {
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
}
```
*See [here](https://github.com/laserdisc-io/laserdisc#example-usage) for an example whit [Laserdisc](https://github.com/laserdisc-io/laserdisc)*

#### Zio Task
To create instances for `ZIO` some useful constructors can be found [here](https://github.com/laserdisc-io/log-effect/blob/master/zio/src/main/scala/log/effect/zio/ZioLogWriter.scala). Note as they exploit the power and expressiveness of the RIO pattern as shown below
```scala
import java.util.{ logging => jul }

import log.effect.zio.ZioLogWriter._
import log.effect.{ LogLevels, LogWriter }
import org.{ log4s => l4s }
import zio.{ RIO, Task }

sealed abstract class App {
  def someZioProgramUsingLogs: RIO[LogWriter[Task], Unit]

  val log4s1: Task[Unit] =
    Task.effect(l4s.getLogger("a logger")) >>= { logger =>
      (log4sFromLogger >>> someZioProgramUsingLogs) provide logger
    }

  val log4s2: Task[Unit] =
    (log4sFromName >>> someZioProgramUsingLogs) provide "a logger name"

  val log4s3: Task[Unit] = {
    case class LoggerClass();
    (log4sFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
  }

  val jul1: Task[Unit] =
    Task.effect(jul.Logger.getLogger("a logger")) >>= { logger =>
      (julFromLogger >>> someZioProgramUsingLogs) provide logger
    }

  val jul2: Task[Unit] =
    julGlobal >>> someZioProgramUsingLogs

  val scribe1: Task[Unit] =
    Task.effect(scribe.Logger("a logger")) >>= { logger =>
      (scribeFromLogger >>> someZioProgramUsingLogs) provide logger
    }

  val scribe2: Task[Unit] =
    (scribeFromName >>> someZioProgramUsingLogs) provide "a logger name"

  val scribe3: Task[Unit] = {
    case class LoggerClass();
    (scribeFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
  }

  val console1: Task[Unit] =
    someZioProgramUsingLogs provide consoleLog

  val console2: Task[Unit] =
    someZioProgramUsingLogs provide consoleLogUpToLevel(LogLevels.Warn)

  val noOp: Task[Unit] =
    someZioProgramUsingLogs provide noOpLog
}
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
**NB:** notice above the `LogWriter`'s implicit evidence given as context bound and the `import log.effect.fs2.interop.show._`. The latter is needed to summon an `internal.Show` given a `cats.Show`.

In some cases like tests a non logging instance might come useful. In such a case the `noOp` logging version is provided. See below an example taken from the [Laserdisc](https://github.com/laserdisc-io/laserdisc)'s tests
```scala
import java.nio.channels.AsynchronousChannelGroup

import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import cats.syntax.flatMap._
import log.effect.fs2.Fs2LogWriter.noOpLogStream

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
): fs2.Stream[F, RedisClient[F]] =
  noOpLogStream >>= { implicit log =>
    RedisClient[F](address)
  }
```
<br>

## License

LaserDisc is licensed under the **[MIT License](LICENSE)** (the "License"); you may not use this software except in
compliance with the License. <br>Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
