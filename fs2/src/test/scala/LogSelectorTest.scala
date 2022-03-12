/*
 * Copyright (c) 2022 LaserDisc
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
import log.effect.LogWriter
import log.effect.fs2.{LogSelector, TestLogCapture}
import log.effect.fs2.SyncLogWriter.consoleLog
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class LogSelectorTest extends AnyWordSpecLike with Matchers with TestLogCapture {

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
            val useIt   = LogSelector[F].log.info("a test")
          }
        )
      )(_ => F.unit)
  }

  "Log selector uses the LogWriter if one is provided in scope" in {
    def useTheClient[F[_]: Sync](address: String): F[Unit] = {
      implicit val logger: LogWriter[F] = consoleLog[F]
      ALoggingClient[F](address).use(cl => cl.useIt)
    }

    val out = capturedConsoleOutOf(useTheClient[IO]("an address"))

    out should include("a test")
  }

  "Log selector defaults to noOpLog if no LogWriter is provided" in {
    def useTheClient[F[_]: Sync](address: String): F[Unit] =
      ALoggingClient[F](address).use(cl => cl.useIt)

    val out = capturedConsoleOutOf(useTheClient[IO]("an address"))

    out shouldBe empty
  }
}
