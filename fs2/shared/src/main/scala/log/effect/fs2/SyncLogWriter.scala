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

package log.effect
package fs2

import java.util.{logging => jul}

import cats.Applicative
import cats.effect.Sync
import log.effect.internal.Id

object SyncLogWriter extends SyncLogWriterPlatformSpecific {
  import instances._

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
}
