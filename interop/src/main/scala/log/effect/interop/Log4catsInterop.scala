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
package interop

import org.typelevel.log4cats.{Logger, MessageLogger}
import log.effect.LogLevels.{Debug, Error, Info, Trace, Warn}
import log.effect.internal.Show
import log.effect.internal.syntax._

private[interop] trait Log4catsInterop0 extends Log4catsInterop1 {
  implicit def logWriterFromLogger[F[_]](implicit ev: Logger[F]): LogWriter[F] =
    new LogWriter[F] {
      def write[A: Show](level: LogLevel, a: =>A): F[Unit] =
        (level, a) match {
          case (Trace, Failure(msg, th)) => ev.trace(th)(msg)
          case (Trace, other)            => ev.trace(other.show)
          case (Debug, Failure(msg, th)) => ev.debug(th)(msg)
          case (Debug, other)            => ev.debug(other.show)
          case (Info, Failure(msg, th))  => ev.info(th)(msg)
          case (Info, other)             => ev.info(other.show)
          case (Warn, Failure(msg, th))  => ev.warn(th)(msg)
          case (Warn, other)             => ev.warn(other.show)
          case (Error, Failure(msg, th)) => ev.error(th)(msg)
          case (Error, other)            => ev.error(other.show)
        }
    }
}

private[interop] trait Log4catsInterop1 {
  implicit def logWriterFromMessageLogger[F[_]](implicit ev: MessageLogger[F]): LogWriter[F] =
    new LogWriter[F] {
      def write[A: Show](level: LogLevel, a: =>A): F[Unit] =
        level match {
          case Trace => ev.trace(a.show)
          case Debug => ev.debug(a.show)
          case Info  => ev.info(a.show)
          case Warn  => ev.warn(a.show)
          case Error => ev.error(a.show)
        }
    }
}
