/*
 * Copyright (c) 2023 LaserDisc
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

import cats.effect.{IO, Resource, Sync}
import cats.syntax.flatMap._
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import log.effect.fs2.LogSelector
import log.effect.interop.TestLogCapture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.slf4j.Logger

final class InteropLogSelectorTest extends AnyWordSpecLike with Matchers with TestLogCapture {

  private[this] sealed trait ALoggingClient[F[_]] {
    def address: String
    def useIt: F[Unit]
  }
  private[this] object ALoggingClient {
    def apply[F[_]: LogSelector](a: String)(
      implicit F: Sync[F]
    ): Resource[F, ALoggingClient[F]] =
      Resource.make(
        F.pure(
          new ALoggingClient[F] {
            val address = a
            val useIt   = LogSelector[F].log.info("this is a test")
          }
        )
      )(_ => F.unit)
  }

  "Log selector can infer the correct log if a log4cats Logger is in scope" in {
    import log.effect.interop.log4cats._

    def buildLog4catsLogger[F[_]: Sync](logger: Logger): F[SelfAwareStructuredLogger[F]] =
      Slf4jLogger.fromSlf4j[F](logger)

    def useLoggingClient[F[_]: Sync](address: String)(logger: Logger): F[Unit] =
      buildLog4catsLogger[F](logger) >>= { implicit l =>
        ALoggingClient[F](address).use(_.useIt)
      }

    val logged = capturedLog4sOutOf(useLoggingClient[IO]("an address")).map(_.message)

    logged shouldBe Seq("this is a test")
  }

  "Log selector will default to no logs if no log4cats Logger is in scope" in {

    def buildLog4catsLogger[F[_]: Sync](logger: Logger): F[SelfAwareStructuredLogger[F]] =
      Slf4jLogger.fromSlf4j[F](logger)

    def useLoggingClient[F[_]: Sync](address: String)(logger: Logger): F[Unit] =
      buildLog4catsLogger[F](logger) >>= { _ =>
        ALoggingClient[F](address).use(_.useIt)
      }

    val logged = capturedLog4sOutOf(useLoggingClient[IO]("an address"))

    logged shouldBe Seq()
  }
}
