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

package log.effect
package fs2

import java.util.{logging => jul}

import _root_.fs2.Stream
import cats.Applicative
import cats.effect.Sync
import log.effect.fs2.SyncLogWriter._
import org.{log4s => l4s}

object Fs2LogWriter {
  def log4sLogStream[F[_]: Sync](l: l4s.Logger): Stream[F, LogWriter[F]] =
    Stream emit log4sLog(l)

  def log4sLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]] =
    Stream eval log4sLog(c)

  def log4sLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval log4sLog(n)

  def julLogStream[F[_]: Sync](l: jul.Logger): Stream[F, LogWriter[F]] =
    Stream emit julLog(l)

  def julLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream eval julLog

  def scribeLogStream[F[_]: Sync](l: scribe.Logger): Stream[F, LogWriter[F]] =
    Stream emit scribeLog(l)

  def scribeLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]] =
    Stream eval scribeLog(c)

  def scribeLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval scribeLog(n)

  def consoleLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream emit consoleLog

  def consoleLogStreamUpToLevel[F[_]: Sync, LL <: LogLevel](minLevel: LL): Stream[F, LogWriter[F]] =
    Stream emit consoleLogUpToLevel(minLevel)

  def noOpLogStream[F[_]: Applicative]: Stream[F, LogWriter[F]] =
    Stream emit noOpLog
}
