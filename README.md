# Log Effect
[![Build Status](https://travis-ci.org/laserdisc-io/log-effect.svg?branch=master)](https://travis-ci.org/laserdisc-io/log-effect)
[![scaladex](https://img.shields.io/badge/scaladex-log--effect-orange.svg)](https://index.scala-lang.org/laserdisc-io/log-effect)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.11.svg?label=core%202.11&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-core_2.12.svg?label=core%202.12&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-core_2.12)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.11.svg?label=fs2%202.11&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.11)
[![Maven Central](https://img.shields.io/maven-central/v/io.laserdisc/log-effect-fs2_2.12.svg?label=fs2%202.12&colorB=2282c3)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/log-effect-fs2_2.12)

[![Join the chat at https://gitter.im/laserdisc-io/laserdisc](https://badges.gitter.im/laserdisc-io/laserdisc.svg)](https://gitter.im/laserdisc-io/laserdisc?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://raw.githubusercontent.com/laserdisc-io/log-effect/master/LICENSE)

## Start
Log effect is available for Scala 2.11.x and 2.12.x. To add it define the dependency in the sbt build. If you need the stream based constructors and syntax you need to depend on the `fs2` lib
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-fs2" % <latest-fs2-version>
```
If the constructors and the syntax in the effect monad are enough just depend on the core 
```scala
libraryDependencies += "io.laserdisc" %% "log-effect-core" % <latest-core-version>
```
for the latest versions available please refer to the badges below the title.

## Dependencies

|                 |        | Fs2    | Log Effect Core   |
| --------------- | ------:| ------:| -----------------:|
| log-effect-fs2  | 0.1.10 | 0.10.5 | 0.1.10             |

<br>

|                 |        | Cats  | Cats Effect | Log4s  |
| --------------- | ------:| -----:| -----------:| ------:|
| log-effect-core | 0.1.10 | 1.1.0 | 0.10.1      | 1.6.1  |

<br>

## Examples

### Get Instances

To get an instance of `LogWriter` the following constructors are availble in the companion object
```scala
import log.effect.LogWriter._

def log4sLog[F[_]: Sync](fa: F[l4s.Logger]): F[LogWriter[F]]

def log4sLog[F[_]: Sync](c: Class[_]): F[LogWriter[F]]

def log4sLog[F[_]: Sync](n: String): F[LogWriter[F]]

def julLog[F[_]: Sync](fa: F[jul.Logger]): F[LogWriter[F]]

def consoleLog[F[_]: Sync]: LogWriter[F]

def noOpLog[F[_]: Applicative]: LogWriter[F]
```

and the following for constructing in a streaming application
```scala
import log.effect.fs2.Fs2LogWriter._

def log4sLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]]

def log4sLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]]

def julLogStream[F[_]: Sync]: Stream[F, LogWriter[F]]

def consoleLogStream[F[_]: Sync]: Stream[F, LogWriter[F]]

def noOpLogStream[F[_]: Sync]: Stream[F, LogWriter[F]]
```

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

implicit final val SC: Scheduler = ???
implicit final val EC: ExecutionContext = ???
implicit final val CG: AsynchronousChannelGroup = ???

def redisClient[F[_]: Effect](address: RedisAddress)(implicit log: LogWriter[F]): Stream[F, RedisClient[F]] =
  RedisClient[F](Set(address)) evalMap (
    client => log.info(s"Laserdisc Redis client for $address") *> Effect[F].pure(client)
  )
```
```scala
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

or still in streams through the mtl style syntax for the singleton type of the companion and the `write` method (notice the `write` method called on the `LogWriter` singleton companion object)
```scala
import cats.Show
import cats.syntax.apply._
import cats.instances.string._
import LogWriter.Debug
import laserdisc.fs2.{RedisAddress, RedisClient}

implicit final val SC: Scheduler = ???
implicit final val EC: ExecutionContext = ???
implicit final val CG: AsynchronousChannelGroup = ???

def redisClient[F[_]: Effect: LogWriter](address: RedisAddress): Stream[F, RedisClient[F]] = {

  // Show instances are needed for every type
  // that needs to be logged
  implicit val addressShow: Show[RedisAddress] = ???
  implicit val clientShow: Show[RedisClient[F]] = ???

  RedisClient[F](Set(address)) evalMap (
    client =>
      LogWriter.write(Debug, "Connecting") *>
      LogWriter.write(Debug, address) *>
      LogWriter.write(Debug, client) *>
      Effect[F].pure(client)
  )
}
```

<br>

## License

LaserDisc is licensed under the **[MIT License](LICENSE)** (the "License"); you may not use this software except in
compliance with the License. <br>Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
