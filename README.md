# Log Effect
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f3d016e4069a445b866ec4a059b23d1c)](https://app.codacy.com/app/barambani/log-effect?utm_source=github.com&utm_medium=referral&utm_content=laserdisc-io/log-effect&utm_campaign=Badge_Grade_Dashboard)
[![Build Status](https://travis-ci.org/laserdisc-io/log-effect.svg?branch=master)](https://travis-ci.org/laserdisc-io/log-effect)
[![scaladex](https://img.shields.io/badge/scaladex-log--effect-orange.svg)](https://index.scala-lang.org/laserdisc-io/log-effect)
[![Join the chat at https://gitter.im/laserdisc-io/laserdisc](https://badges.gitter.im/laserdisc-io/laserdisc.svg)](https://gitter.im/laserdisc-io/laserdisc?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE)

[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.11.svg?label=core%202.11&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=core%202.12&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.11.svg?label=fs2%202.11&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.12.svg?label=fs2%202.12&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.12)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.11.svg?label=zio%202.11&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.12.svg?label=zio%202.12&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.12)


## Start
Log Effect is available for Scala 2.11.x and 2.12.x. Helper constructors are provided for **Cats Effect**'s `Sync` `F[_]`, for **Fs2**'s `Stream` and for **Scalaz ZIO**'s `IO`. 
Add
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-fs2" % <latest-fs2-version>
```
for **Fs2** or **Cats Effect**. Add instead
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-zio" % <latest-zio-version>
```
for **Scalaz ZIO**. If the intention is to create your own instance for the typeclass, adding
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-core" % <latest-core-version>
```
will be enough. For the latest versions available please refer to the badges below the title.

## Dependencies

|                          | Fs2    | Cats Effect | Log Effect Core   |
| ------------------------:| ------:| -----------:| -----------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.12.svg?label=log-effect-fs2&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.12) | 1.0.0-M5 | 1.0.0 | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12) |
| v0.2.2  | 0.10.5 | 0.10.1 | v0.2.2  |
| v0.1.14 | 0.10.5 |        | v0.1.14 |

<br>

|                          | Scalaz ZIO | Log Effect Core   |
| ------------------------:| ----------:| -----------------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-zio_2.12.svg?label=log-effect-zio&colorB=fb0005)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-zio_2.12) | 0.2.7 | [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=%20&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12) 

<br>

|                          | Cats  | Cats Effect | Log4s  |
| ------------------------:| -----:| -----------:| ------:|
| [![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=log-effect-core&colorB=9311fc)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12) |  |  | 1.6.1  |
| v0.2.2  | 1.2.0 |             | 1.6.1  |
| v0.1.14 | 1.2.0 | 0.10.1      | 1.6.1  |

<br>

## Examples

### Get Instances

To get an instance of `LogWriter` for **Cats Effect**'s `Sync`
```scala
import log.effect.fs2.SyncLogWriter._

def log4sLog[F[_]: Sync](fa: F[l4s.Logger]): F[LogWriter[F]]

def log4sLog[F[_]: Sync](c: Class[_]): F[LogWriter[F]]

def log4sLog[F[_]: Sync](n: String): F[LogWriter[F]]

def julLog[F[_]: Sync](fa: F[jul.Logger]): F[LogWriter[F]]

def julLog[F[_]: Sync]: F[LogWriter[F]] // will use jul.Logger.getGlobal

def scribeLog[F[_]: Sync](fa: F[scribe.Logger]): F[LogWriter[F]]

def scribeLog[F[_]: Sync](c: Class[_])(
  implicit ev: Class[_] <:< scribe.Logger
): F[LogWriter[F]]

def scribeLog[F[_]: Sync](n: String): F[LogWriter[F]]

def consoleLog[F[_]: Sync]: LogWriter[F]

// for a console logger that will write only up to the specified level
// 
// val log = consoleLogUpToLevel[F](LogLevels.Info)
// 
def consoleLogUpToLevel[F[_]]: ConsoleLogPartial[F]

def noOpLog[F[_]: Applicative]: LogWriter[F]
```

To get an instance of `LogWriter` for **Fs2**'s `Stream`
```scala
import log.effect.fs2.Fs2LogWriter._

def log4sLogStream[F[_]: Sync](fa: F[l4s.Logger]): Stream[F, LogWriter[F]]

def log4sLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]]

def log4sLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]]

def julLogStream[F[_]: Sync](fa: F[jul.Logger]): Stream[F, LogWriter[F]]

def julLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] // will use jul.Logger.getGlobal

def scribeLogStream[F[_]: Sync](fa: F[scribe.Logger]): Stream[F, LogWriter[F]]

def scribeLogStream[F[_]: Sync](c: Class[_])(
  implicit ev: Class[_] <:< scribe.Logger
): Stream[F, LogWriter[F]]

def scribeLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]]

def consoleLogStream[F[_]: Sync]: Stream[F, LogWriter[F]]

// for a console logger that will write only up to the specified level 
// 
// val log = consoleLogStreamUpToLevel[F](LogLevels.Info)
// 
def consoleLogStreamUpToLevel[F[_]]: ConsoleLogStreamPartial[F]

def noOpLogStream[F[_]: Sync]: Stream[F, LogWriter[F]]
```
*See [here](https://github.com/laserdisc-io/laserdisc#example-usage) for an example whit [Laserdisc](https://github.com/laserdisc-io/laserdisc)*

To get an instance for **Scalaz ZIO**'s `IO` use the ones below
```scala
import log.effect.zio.ZioLogWriter._

def log4sLogZio(fa: ExIO[l4s.Logger]): ExIO[LogWriter[ExIO]]

def log4sLogZio(c: Class[_]): ExIO[LogWriter[ExIO]]

def log4sLogZio[F[_]](n: String): ExIO[LogWriter[ExIO]]

def julLogZio[F[_]](fa: ExIO[jul.Logger]): ExIO[LogWriter[ExIO]]

def consoleLogZio: LogWriter[IO[IOException, ?]]

// for a console logger that will write only up to the specified level 
// 
// val log = consoleLogZioUpToLevel(LogLevels.Info)
// 
def consoleLogZioUpToLevel: ConsoleLogZioPartial

def noOpLogZio: LogWriter[IO[Nothing, ?]]
```
where `ExIO` is defined as `IO[Exception, ?]`


### Submit Logs

In a monadic sequence of computations
```scala
import cats.syntax.functor._
import cats.syntax.flatMap._

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

or in a streaming environment using the `LogWriter`'s or the `fs2` streams' syntaxes
```scala
import cats.syntax.apply._
import laserdisc.fs2.{RedisAddress, RedisClient}

implicit final val SC: Scheduler = ???
implicit final val EC: ExecutionContext = ???
implicit final val CG: AsynchronousChannelGroup = ???

def redisClient[F[_]: Effect](address: RedisAddress)(implicit log: LogWriter[F]): Stream[F, RedisClient[F]] =
  RedisClient[F](Set(address)) evalMap (
    client => log.info(s"Laserdisc Redis client for $address") *> Effect[F].pure(client)
  )
```
```scala
import laserdisc.fs2.{RedisAddress, RedisClient}

implicit final val SC: Scheduler = ???
implicit final val EC: ExecutionContext = ???
implicit final val CG: AsynchronousChannelGroup = ???

def redisCache[F[_]: Effect](address: RedisAddress)(implicit log: LogWriter[F]): Stream[F, RedisClient[F]] =
  for {
    _      <- log.infoS(s"About to connect a Laserdisc Redis client to $address")
    client <- RedisClient[F](Set(address))
    _      <- log.infoS("The connection went fine. Talking to the server")
  } yield client
```

or through the mtl style syntax for the singleton type of the companion (notice the syntax or the `write` method called on the `LogWriter`'s companion object)
```scala
import cats.effect.Sync
import cats.syntax.apply._
import log.effect.LogWriter

def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] = {

  source evalMap (
    n =>
      LogWriter.debug("Processing a number") *>
        LogWriter.debug(n.toString) *>
        Sync[F].pure(n * 2) <*
        LogWriter.debug("Processed")
  ) handleErrorWith (
    th =>
      fs2.Stream.eval(
        LogWriter.error("Ops, something didn't work", th) *> Sync[F].pure(0)
      )
  )
}
```
```scala
import cats.Show
import cats.syntax.apply._
import cats.syntax.either._
import cats.instances.string._
import LogWriter.{Debug, Error, Failure}
import laserdisc.fs2.{RedisAddress, RedisClient}
import log.effect.fs2.implicits._

type |[A, B] = Either[A, B]

implicit final val SC: Scheduler = ???
implicit final val EC: ExecutionContext = ???
implicit final val CG: AsynchronousChannelGroup = ???

def redisClient[F[_]: Effect: LogWriter](address: RedisAddress): Stream[F, Throwable | RedisClient[F]] = {

  // Show instances are needed for every logged type
  implicit val addressShow: Show[RedisAddress] = ???
  implicit val clientShow: Show[RedisClient[F]] = ???

  RedisClient[F](Set(address)) evalMap (
    client =>
      LogWriter.write(Debug, "Connecting") *>
      LogWriter.write(Debug, address) *>
      LogWriter.write(Debug, client) *>
      Effect[F].pure(client.asRight)
  ) handleErrorWith (
    th => Stream.eval(
      LogWriter.write(Error, Failure("Ops, something didn't work", th)) *> Effect[F].pure(th.asLeft)
    )
  )
}
```
```scala
import cats.Show
import cats.effect.Sync
import cats.syntax.apply._
import log.effect.LogLevels.{ Debug, Error }
import log.effect.LogWriter
import log.effect.LogWriter.Failure
import log.effect.fs2.implicits._

def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] = {

  // Show instances are needed for every logged type
  implicit def intShow: Show[Int] = ???

  source evalMap (
    n =>
      LogWriter.write(Debug, "Processing a number") *>
        LogWriter.write(Debug, n) *>
        Sync[F].pure(n * 2) <*
        LogWriter.write(Debug, "Processed")
  ) handleErrorWith (
    th =>
      fs2.Stream.eval(
        LogWriter.write(Error, Failure("Ops, something didn't work", th)) *> Sync[F].pure(0)
      )
  )
}
```
**NB:** notice the `LogWriter`'s implicit evidence given as context bound and the `import log.effect.fs2.implicits._`. The latter is needed to summon an `internal.Show` instance given the `cats.Show`.

<br>

## License

LaserDisc is licensed under the **[MIT License](LICENSE)** (the "License"); you may not use this software except in
compliance with the License. <br>Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
