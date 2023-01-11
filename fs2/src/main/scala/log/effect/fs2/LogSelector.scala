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

package log.effect
package fs2

import cats.Applicative

/**
 * It helps to add logging capability to components allowing them to use a `LogWriter[F]` if in
 * scope or defaulting, when F[_] is an Applicative, to noOpLog[F] if no `LogWriter` is provided.
 *
 * Example:
 *
 * def create[F[_]: LogSelector](a: String): ALoggingClient[F] = new ALoggingClient[F] { def do =
 * LogSelector[F].writer.info("something") }
 *
 * def useTheClient[F[_]](address: String): F[Unit] = { implicit val logger: LogWriter[F] =
 * consoleLog[F] create[F](address) } will log using `logger`
 *
 * def useTheClient[F[_]](address: String): F[Unit] = create[F](address) will not log
 */
final class LogSelector[F[_]](val log: LogWriter[F]) extends AnyVal

object LogSelector extends LogSelectorInstances0 {
  @inline final def apply[F[_]](implicit i: LogSelector[F]): LogSelector[F] = i
}

private[fs2] sealed trait LogSelectorInstances0 extends LogSelectorInstances1 {
  implicit def otherLogWriter[F[_]](implicit lw: LogWriter[F]): LogSelector[F] =
    new LogSelector(lw)
}

private[fs2] sealed trait LogSelectorInstances1 {
  implicit def noOpLogWriter[F[_]: Applicative]: LogSelector[F] =
    new LogSelector(SyncLogWriter.noOpLog[F])
}
