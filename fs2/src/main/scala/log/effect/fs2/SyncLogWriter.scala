/*
 * Copyright (c) 2024 LaserDisc
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

package log.effect
package fs2

import java.util.{logging => jul}

import cats.Applicative
import cats.effect.Sync
import log.effect.internal.{EffectSuspension, Id, Show}
import org.{log4s => l4s}

object SyncLogWriter {
  import instances._

  def log4sLog[F[_]: Sync](l: l4s.Logger): LogWriter[F] =
    LogWriter.pureOf[F](l)

  def log4sLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(l4s.getLogger(c)))

  def log4sLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(l4s.getLogger(n)))

  def julLog[F[_]: Sync](l: jul.Logger): LogWriter[F] =
    LogWriter.pureOf[F](l)

  def julLog[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(jul.Logger.getGlobal))

  def scribeLog[F[_]: Sync](l: scribe.Logger): LogWriter[F] =
    LogWriter.pureOf[F](l)

  def scribeLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(scribe.Logger(n)))

  def scribeLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] = {
    import scribe._
    LogWriter.of(F.delay(c.logger))
  }

  def consoleLog[F[_]: Sync]: LogWriter[F] =
    LogWriter.pureOf[F](LogLevels.Trace)

  def consoleLogUpToLevel[F[_]: Sync, LL <: LogLevel](minLevel: LL): LogWriter[F] =
    LogWriter.pureOf[F](minLevel)

  def noOpLog[F[_]: Applicative]: LogWriter[F] =
    LogWriter.of[Id](()).liftF

  private[this] object instances {
    private[fs2] implicit final def syncInstances[F[_]](implicit F: Sync[F]): EffectSuspension[F] =
      new EffectSuspension[F] {
        def suspend[A](a: =>A): F[A] = F delay a
      }

    private[fs2] implicit final def functorInstances[F[_]](
      implicit F: cats.Functor[F]
    ): internal.Functor[F] =
      new internal.Functor[F] {
        def fmap[A, B](f: A => B): F[A] => F[B] = F lift f
      }

    implicit final class NoOpLogF(private val _underlying: LogWriter[Id]) extends AnyVal {
      def liftF[F[_]: Applicative]: LogWriter[F] =
        new LogWriter[F] {
          private[this] val unit                               = Applicative[F].unit
          def write[A: Show](level: LogLevel, a: =>A): F[Unit] = unit
        }
    }
  }
}
