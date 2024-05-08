/*
 * Copyright (c) 2018-2024 LaserDisc
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

import cats.Applicative
import cats.syntax.functor._
import log.effect.internal
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.annotation.nowarn

final class LogWriterResolutionTest extends AnyWordSpecLike with Matchers {
  "the construction" should {
    "correctly infer a valid log4s constructor for an F[_] given an implicit evidence of Sync[F]" in {
      import cats.effect.Sync
      import log.effect.fs2.SyncLogWriter.log4sLog
      import log.effect.internal.{EffectSuspension, Functor}
      import log.effect.{LogWriter, LogWriterConstructor}
      import org.log4s

      def c[F[_]]: F[org.log4s.Logger] => F[LogWriter[F]] = {
        implicit def F: EffectSuspension[F] = ???
        implicit def FF: Functor[F]         = ???
        implicitly[LogWriterConstructor[log4s.Logger, F, F]].construction
      }

      def cPure[F[_]]: org.log4s.Logger => LogWriter[F] = {
        implicit def F: EffectSuspension[F] = ???
        implicitly[LogWriterConstructor[log4s.Logger, internal.Id, F]].construction
      }

      @nowarn def l1[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
        c(F.delay(org.log4s.getLogger("test")))

      @nowarn def pureL1[F[_]]: LogWriter[F] =
        cPure[F](org.log4s.getLogger("test"))

      @nowarn def l2[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
        F.delay(org.log4s.getLogger("test")) map log4sLog[F]
    }

    "correctly infer a valid jul constructor for an F[_] given an implicit evidence of Sync[F]" in {
      import java.util.{logging => jul}

      import cats.effect.Sync
      import log.effect.fs2.SyncLogWriter.julLog
      import log.effect.internal.{EffectSuspension, Functor}
      import log.effect.{LogWriter, LogWriterConstructor}

      def c[F[_]]: F[java.util.logging.Logger] => F[LogWriter[F]] = {
        implicit def F: EffectSuspension[F] = ???
        implicit def FF: Functor[F]         = ???
        implicitly[LogWriterConstructor[jul.Logger, F, F]].construction
      }

      def cPure[F[_]]: java.util.logging.Logger => LogWriter[F] = {
        implicit def F: EffectSuspension[F] = ???
        implicitly[LogWriterConstructor[jul.Logger, internal.Id, F]].construction
      }

      @nowarn def l1[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
        c(F.delay(java.util.logging.Logger.getGlobal))

      @nowarn def pureL1[F[_]]: LogWriter[F] =
        cPure[F](java.util.logging.Logger.getGlobal)

      @nowarn def l2[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
        F.delay(java.util.logging.Logger.getGlobal) map julLog[F]
    }

    "not infer a valid log4s constructor for an F[_] if there is no implicit evidence of EffectSuspension[F]" in {
      """
        |import log.effect.{LogWriter, LogWriterConstructor}
        |import log.effect.internal.Functor
        |import org.log4s
        |
        |def c[F[_]]: F[org.log4s.Logger] => F[LogWriter[F]] = {
        |  implicit def FF: Functor[F]         = ???
        |  implicitly[LogWriterConstructor[F[log4s.Logger], F, F]].construction
        |}
      """.stripMargin shouldNot compile
    }

    "correctly infer a valid console constructor for an F[_] given an implicit evidence of Sync[F]" in {
      import cats.effect.Sync
      import log.effect.fs2.SyncLogWriter.{consoleLog, consoleLogUpToLevel}
      import log.effect.internal.{EffectSuspension, Id}
      import log.effect.{LogLevel, LogLevels, LogWriter, LogWriterConstructor}

      def c[F[_], L <: LogLevel]: L => LogWriter[F] = {
        implicit def F: EffectSuspension[F] = ???
        implicitly[LogWriterConstructor[L, Id, F]].construction
      }

      @nowarn def l1[F[_]]: LogWriter[F] =
        c[F, LogLevels.Trace](LogLevels.Trace)

      @nowarn def l2[F[_]: Sync]: LogWriter[F] =
        consoleLog[F]

      @nowarn def l3[F[_]]: LogWriter[F] =
        c[F, LogLevels.Info](LogLevels.Info)

      @nowarn def l4[F[_]: Sync]: LogWriter[F] =
        consoleLogUpToLevel(LogLevels.Info)
    }

    "correctly infer a valid no-op constructor for an F[_] given an implicit evidence" in {
      import log.effect.fs2.SyncLogWriter.noOpLog
      import log.effect.internal.Id
      import log.effect.{LogWriter, LogWriterConstructor}

      def c: Unit => LogWriter[Id] =
        implicitly[LogWriterConstructor[Unit, Id, Id]].construction

      @nowarn def l1: LogWriter[Id] = c(())

      @nowarn def l2[F[_]: Applicative]: LogWriter[F] = noOpLog[F]
    }

    "correctly infer a valid log4s constructor for IO" in {
      import cats.effect.IO
      import log.effect.fs2.SyncLogWriter.log4sLog
      import log.effect.internal.{EffectSuspension, Functor}
      import log.effect.{LogWriter, LogWriterConstructor}
      import org.log4s

      def c: IO[org.log4s.Logger] => IO[LogWriter[IO]] = {
        implicit def F: EffectSuspension[IO] = ???
        implicit def FF: Functor[IO]         = ???
        implicitly[LogWriterConstructor[log4s.Logger, IO, IO]].construction
      }

      def cPure: org.log4s.Logger => LogWriter[IO] = {
        implicit def F: EffectSuspension[IO] = ???
        implicitly[LogWriterConstructor[log4s.Logger, internal.Id, IO]].construction
      }

      @nowarn def l1: IO[LogWriter[IO]] =
        c(IO.delay(org.log4s.getLogger("test")))

      @nowarn def pureL1: LogWriter[IO] =
        cPure(org.log4s.getLogger("test"))

      @nowarn def l2: IO[LogWriter[IO]] =
        IO.delay(org.log4s.getLogger("test")) map log4sLog[IO]
    }

    "correctly infer a valid jul constructor for IO" in {
      import java.util.{logging => jul}

      import cats.effect.IO
      import log.effect.fs2.SyncLogWriter.julLog
      import log.effect.internal.{EffectSuspension, Functor}
      import log.effect.{LogWriter, LogWriterConstructor}

      def c: IO[java.util.logging.Logger] => IO[LogWriter[IO]] = {
        implicit def F: EffectSuspension[IO] = ???
        implicit def FF: Functor[IO]         = ???
        implicitly[LogWriterConstructor[jul.Logger, IO, IO]].construction
      }

      def cPure: java.util.logging.Logger => LogWriter[IO] = {
        implicit def F: EffectSuspension[IO] = ???
        implicitly[LogWriterConstructor[jul.Logger, internal.Id, IO]].construction
      }

      @nowarn def l1: IO[LogWriter[IO]] =
        c(IO.delay(java.util.logging.Logger.getGlobal))

      @nowarn def pureL1: LogWriter[IO] =
        cPure(java.util.logging.Logger.getGlobal)

      @nowarn def l2: IO[LogWriter[IO]] =
        IO.delay(java.util.logging.Logger.getGlobal) map julLog[IO]
    }
  }

  "the LogWriterConstructor0 of IO" should {
    "correctly infer a valid console constructor for IO" in {
      import cats.effect.IO
      import log.effect.fs2.SyncLogWriter.{consoleLog, consoleLogUpToLevel}
      import log.effect.internal.{EffectSuspension, Id}
      import log.effect.{LogLevel, LogLevels, LogWriter, LogWriterConstructor}

      def c[L <: LogLevel]: L => LogWriter[IO] = {
        implicit def F: EffectSuspension[IO] = ???
        implicitly[LogWriterConstructor[L, Id, IO]].construction
      }

      @nowarn def l1: LogWriter[IO] = c(LogLevels.Trace)

      @nowarn def l2: LogWriter[IO] = consoleLog[IO]

      @nowarn def l3: LogWriter[IO] = c(LogLevels.Info)

      @nowarn def l4: LogWriter[IO] = consoleLogUpToLevel(LogLevels.Info)
    }

    "not be able to infer a no-op constructor for IO without lifting (see SyncLogWriter.noOpLog)" in {
      """
        |import cats.effect.IO
        |import log.effect.internal.Id
        |import log.effect.{LogWriter, LogWriterConstructor}
        |
        |def c: Unit => LogWriter[IO] =
        |  implicitly[LogWriterConstructor[Unit, Id, IO]].construction
        |
        |def l: LogWriter[IO] = c(())
      """.stripMargin shouldNot compile
    }
  }

  "the LogWriter's mtl style construction" should {
    "infer the log when a cats.Show is present and the companion object syntax is used" in {
      import cats.effect.Sync
      import cats.syntax.apply._
      import log.effect.LogWriter

      @nowarn def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] =
        source evalMap { n =>
          LogWriter.debug("Processing a number") *>
            LogWriter.debug(n.toString) *>
            Sync[F].pure(n * 2) <*
            LogWriter.debug("Processed")
        } handleErrorWith { th =>
          fs2.Stream eval (
            LogWriter.error("Ops, something didn't work", th) *> Sync[F].pure(0)
          )
        }
    }

    "be able to infer the log when a cats.Show is present and the `write` construction is used" in {
      import cats.Show
      import cats.effect.Sync
      import cats.syntax.apply._
      import log.effect.LogLevels.{Debug, Error}
      import log.effect.fs2.interop.show._
      import log.effect.{Failure, LogWriter}

      @nowarn def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] = {
        // Show instances are needed for every logged type
        implicit def addressShow: Show[Int] = ???

        source evalMap { n =>
          LogWriter.write(Debug, "Processing a number") *>
            LogWriter.write(Debug, n) *>
            Sync[F].pure(n * 2) <*
            LogWriter.write(Debug, "Processed")
        } handleErrorWith { th =>
          fs2.Stream eval (
            LogWriter.write(Error, Failure("Ops, something didn't work", th)) *> Sync[F].pure(0)
          )
        }
      }
    }

    "be able to summon the LogWriter for ReaderT when LogWriter for `F` is in scope and `mtl.readerT.readerTLogWriter` is imported" in {
      import cats.data.ReaderT
      import cats.effect.Sync
      import cats.syntax.flatMap._
      import log.effect.LogWriter
      import log.effect.fs2.SyncLogWriter._
      import log.effect.fs2.mtl.readerT.readerTLogWriter

      def aLogger[F[_], Env](implicit F: LogWriter[ReaderT[F, Env, *]]): ReaderT[F, Env, Unit] =
        F.info("A message")

      @nowarn def anotherLogger[F[_]: Sync, Env](env: Env): F[Unit] =
        log4sLog[F]("A log") >>= { implicit log => aLogger[F, Env].run(env) }
    }
  }
}
