/*
 * Copyright (c) 2018-2025 LaserDisc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.annotation.nowarn

@nowarn final class ReadmeLogSubmissionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "`in a monadic sequence of effects` snippet should compile" in {
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
  }

  "`in a streaming environment using `LogWriter`'s syntax` snippet should compile" in {
    import java.nio.channels.AsynchronousChannelGroup

    import cats.effect.Async
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

    def redisClient[F[_]: Async](
      address: String
    )(implicit log: LogWriter[F]): fs2.Stream[F, RedisClient[F]] =
      RedisClient[F](address) evalMap { client =>
        log.info(s"Laserdisc Redis client for $address") >> Async[F].pure(client)
      }
  }

  "`in a streaming environment using `fs2` streams' syntax` snippet should compile" in {
    import java.nio.channels.AsynchronousChannelGroup

    import cats.effect.Async
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

    def redisCache[F[_]: Async](
      address: String
    )(implicit log: LogWriter[F]): fs2.Stream[F, RedisClient[F]] =
      for {
        _      <- log.infoS(s"About to connect a Laserdisc Redis client for $address")
        client <- RedisClient[F](address)
        _      <- log.infoS("The connection went fine. Talking to the server")
      } yield client
  }

  "`through the companion's syntax` snippet should compile" in {
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
  }

  "`through the companion's syntax for `fs2` streams` snippet should compile" in {
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
  }

  "`through the companion's accessor for the `write` method` 1 snippet should compile" in {
    import java.nio.channels.AsynchronousChannelGroup

    import cats.Show
    import cats.effect.Async
    import cats.instances.string._
    import cats.syntax.either._
    import cats.syntax.flatMap._
    import log.effect.LogLevels.{Debug, Error}
    import log.effect.fs2.interop.show._
    import log.effect.{Failure, LogWriter}

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

    def redisClient[F[_]: Async: LogWriter](
      address: String
    ): fs2.Stream[F, Throwable | RedisClient[F]] = {
      // Cats Show instances are needed for every logged type
      implicit val clientShow: Show[RedisClient[F]] = ???

      RedisClient[F](address) evalMap { client =>
        LogWriter.write(Debug, "Connected client details:") >> // Or
          LogWriter.debug(address) >>                          // And
          LogWriter.debug(client) >>
          Async[F].pure(client.asRight)
      } handleErrorWith { th =>
        fs2.Stream eval (
          LogWriter.write(
            Error,
            Failure("Ops, something didn't work", th)
          ) >> Async[F].pure(th.asLeft)
        )
      }
    }
  }

  "`or using the fs2 Stream specific syntax like `writeS` or the level alternatives for types that provide a `cats.Show` instance` snippet should compile" in {
    import cats.Show
    import cats.effect.Sync
    import log.effect.LogLevels.Error
    import log.effect.{Failure, LogWriter}
    import log.effect.fs2.syntax._

    trait A
    object A {
      def empty: A                = ???
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
  }
}
