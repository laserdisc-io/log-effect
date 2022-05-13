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

package log.effect

import log.effect.internal.{Id, Show}

import scala.annotation.nowarn

trait LogWriter[F[_]] {
  def write[A: Show](level: LogLevel, a: =>A): F[Unit]
}

object LogWriter extends LogWriterSyntax {
  @inline final def pureOf[F[_]]: logWriterInstancePartial[Id, F] =
    new logWriterInstancePartial[Id, F]

  @inline final def of[F[_]]: logWriterInstancePartial[F, F] =
    new logWriterInstancePartial

  private[effect] final class logWriterInstancePartial[G[_], F[_]](private val d: Boolean = true)
      extends AnyVal {
    @inline def apply[R](read: G[R])(
      implicit instance: LogWriterConstructor[R, G, F]
    ): G[LogWriter[F]] =
      instance construction read
  }
}

private[effect] sealed trait LogWriterSyntax extends LogWriterAliasingSyntax {
  implicit def loggerSyntax[T, F[_]](l: LogWriter[F]): LogWriterOps[F] =
    new LogWriterOps(l)
}

private[effect] sealed trait LogWriterAliasingSyntax {
  @nowarn implicit def logWriterSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriter[F] = LW

  @nowarn implicit def logWriterOpsSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriterOps[F] = new LogWriterOps(LW)
}

private[effect] final class LogWriterOps[F[_]](private val aLogger: LogWriter[F]) extends AnyVal {
  import LogLevels._

  @inline def trace[A: Show](a: =>A): F[Unit] =
    aLogger.write(Trace, a)

  @inline def trace(msg: =>String): F[Unit] =
    aLogger.write(Trace, msg)

  @inline def trace(th: =>Throwable)(implicit _dummy: DummyImplicit): F[Unit] =
    aLogger.write(Trace, th)

  @inline def trace(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Trace, Failure(msg, th))

  @inline def debug[A: Show](a: =>A): F[Unit] =
    aLogger.write(Debug, a)

  @inline def debug(msg: =>String): F[Unit] =
    aLogger.write(Debug, msg)

  @inline def debug(th: =>Throwable)(implicit _dummy: DummyImplicit): F[Unit] =
    aLogger.write(Debug, th)

  @inline def debug(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Debug, Failure(msg, th))

  @inline def info[A: Show](a: =>A): F[Unit] =
    aLogger.write(Info, a)

  @inline def info(msg: =>String): F[Unit] =
    aLogger.write(Info, msg)

  @inline def info(th: =>Throwable)(implicit _dummy: DummyImplicit): F[Unit] =
    aLogger.write(Info, th)

  @inline def info(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Info, Failure(msg, th))

  @inline def error[A: Show](a: =>A): F[Unit] =
    aLogger.write(Error, a)

  @inline def error(msg: =>String): F[Unit] =
    aLogger.write(Error, msg)

  @inline def error(th: =>Throwable)(implicit _dummy: DummyImplicit): F[Unit] =
    aLogger.write(Error, th)

  @inline def error(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Error, Failure(msg, th))

  @inline def warn[A: Show](a: =>A): F[Unit] =
    aLogger.write(Warn, a)

  @inline def warn(msg: =>String): F[Unit] =
    aLogger.write(Warn, msg)

  @inline def warn(th: =>Throwable)(implicit _dummy: DummyImplicit): F[Unit] =
    aLogger.write(Warn, th)

  @inline def warn(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Warn, Failure(msg, th))
}
