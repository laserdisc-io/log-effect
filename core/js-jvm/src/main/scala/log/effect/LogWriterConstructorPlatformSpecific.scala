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

import org.{log4s => l4s}

trait LogWriterConstructorPlatformSpecific {
  import internal._
  import internal.syntax._

  implicit def log4sConstructor[G[_]: Functor, F[_]](
    implicit F: EffectSuspension[F]
  ): LogWriterConstructor[l4s.Logger, G, F] =
    new LogWriterConstructor[l4s.Logger, G, F] {
      val construction: G[l4s.Logger] => G[LogWriter[F]] =
        _ map { l4sLogger =>
          new LogWriter[F] {
            def write[A: Show](level: LogLevel, a: =>A): F[Unit] = {
              val beLevel = level match {
                case LogLevels.Trace => l4s.Trace
                case LogLevels.Debug => l4s.Debug
                case LogLevels.Info  => l4s.Info
                case LogLevels.Error => l4s.Error
                case LogLevels.Warn  => l4s.Warn
              }

              F.suspend(
                a match {
                  case Failure(msg, th) => l4sLogger(beLevel)(th)(msg)
                  case _                => l4sLogger(beLevel)(a.show)
                }
              )
            }
          }
        }
    }
}
