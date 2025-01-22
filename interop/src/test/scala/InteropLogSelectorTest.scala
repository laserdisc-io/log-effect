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

import cats.effect.{IO, Resource, Sync}
import cats.syntax.flatMap._
import log.effect.fs2.LogSelector
import org.typelevel.log4cats.testing.StructuredTestingLogger
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class InteropLogSelectorTest extends AnyWordSpecLike with Matchers {

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

  private[this] def testLogger[F[_]: Sync]: F[StructuredTestingLogger[F]] =
    Sync[F].pure(StructuredTestingLogger.impl())

  "Log selector can infer the correct log if a log4cats Logger is in scope" in {
    import log.effect.interop.log4cats._

    def useLoggingClient[F[_]: Sync](address: String): F[StructuredTestingLogger[F]] =
      testLogger[F].flatTap { implicit l =>
        ALoggingClient[F](address).use(_.useIt)
      }

    val logged =
      useLoggingClient[IO]("an address").flatMap(_.logged).syncStep(Int.MaxValue).unsafeRunSync()

    logged shouldBe Right(Seq(StructuredTestingLogger.INFO("this is a test", None, Map.empty)))
  }

  "Log selector will default to no logs if no log4cats Logger is in scope" in {

    def useLoggingClient[F[_]: Sync](address: String): F[StructuredTestingLogger[F]] =
      testLogger[F].flatTap { _ =>
        ALoggingClient[F](address).use(_.useIt)
      }

    val logged =
      useLoggingClient[IO]("an address").flatMap(_.logged).syncStep(Int.MaxValue).unsafeRunSync()

    logged shouldBe Right(Seq())
  }
}
