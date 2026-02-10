/*
 * Copyright (c) 2018-2026 LaserDisc
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

import cats.syntax.functor._
import log.effect.internal
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.annotation.nowarn

final class LogWriterResolutionLog4sTest extends AnyWordSpecLike with Matchers {
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
  }
}
