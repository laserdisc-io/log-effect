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
package syntax

import _root_.fs2.Stream

import scala.annotation.nowarn

private[syntax] trait Fs2LogEffectSyntax extends Fs2LogEffectCompanionSyntax {
  implicit def fs2LogEffectSyntax[F[_]](aLogWriter: LogWriter[F]): Fs2LogEffectOps[F] =
    new Fs2LogEffectOps(aLogWriter)
}

private[syntax] sealed trait Fs2LogEffectCompanionSyntax {
  implicit def fs2LogEffectSyntaxSingleton[F[_]](@nowarn _underlying: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): Fs2LogEffectOps[F] =
    new Fs2LogEffectOps(LW)
}

private[syntax] final class Fs2LogEffectOps[F[_]](private val aLogWriter: LogWriter[F])
    extends AnyVal {
  @inline
  def writeS[A: cats.Show](level: LogLevel, a: =>A): Stream[F, Unit] =
    Stream eval aLogWriter.write(level, a)

  @inline
  def traceS[A: cats.Show](a: =>A): Stream[F, Unit] =
    Stream eval aLogWriter.trace(a)

  @inline
  def traceS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.trace(msg)

  @inline
  def traceS(th: =>Throwable)(implicit _dummy: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.trace(th)

  @inline
  def traceS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.trace(msg, th)

  @inline
  def debugS[A: cats.Show](a: =>A): Stream[F, Unit] =
    Stream eval aLogWriter.debug(a)

  @inline
  def debugS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.debug(msg)

  @inline
  def debugS(th: =>Throwable)(implicit _dummy: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.debug(th)

  @inline
  def debugS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.debug(msg, th)

  @inline
  def infoS[A: cats.Show](a: =>A): Stream[F, Unit] =
    Stream eval aLogWriter.info(a)

  @inline
  def infoS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.info(msg)

  @inline
  def infoS(th: =>Throwable)(implicit _dummy: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.info(th)

  @inline
  def infoS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.info(msg, th)

  @inline
  def errorS[A: cats.Show](a: =>A): Stream[F, Unit] =
    Stream eval aLogWriter.error(a)

  @inline
  def errorS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.error(msg)

  @inline
  def errorS(th: =>Throwable)(implicit _dummy: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.error(th)

  @inline
  def errorS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.error(msg, th)

  @inline
  def warnS[A: cats.Show](a: =>A): Stream[F, Unit] =
    Stream eval aLogWriter.warn(a)

  @inline
  def warnS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.warn(msg)

  @inline
  def warnS(th: =>Throwable)(implicit _dummy: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.warn(th)

  @inline
  def warnS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.warn(msg, th)
}
