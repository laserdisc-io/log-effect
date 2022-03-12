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

import java.util.{logging => jul}
import log.effect.internal._
import log.effect.internal.syntax._
import org.{log4s => l4s}
import scribe.message.Message

sealed trait LogWriterConstructor[R, G[_], F[_]] {
  def construction: G[R] => G[LogWriter[F]]
}

object LogWriterConstructor {
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

  implicit def julConstructor[G[_]: Functor, F[_]](
    implicit F: EffectSuspension[F]
  ): LogWriterConstructor[jul.Logger, G, F] =
    new LogWriterConstructor[jul.Logger, G, F] {
      val construction: G[jul.Logger] => G[LogWriter[F]] =
        _ map { julLogger =>
          new LogWriter[F] {
            def write[A: Show](level: LogLevel, a: =>A): F[Unit] = {
              val beLevel = level match {
                case LogLevels.Trace => jul.Level.FINEST
                case LogLevels.Debug => jul.Level.FINE
                case LogLevels.Info  => jul.Level.INFO
                case LogLevels.Warn  => jul.Level.WARNING
                case LogLevels.Error => jul.Level.SEVERE
              }

              F.suspend(
                if (julLogger.isLoggable(beLevel))
                  julLogger.log(
                    a match {
                      case Failure(msg, th) =>
                        val r = new jul.LogRecord(beLevel, msg)
                        r.setThrown(th)
                        r
                      case _ => new jul.LogRecord(beLevel, a.show)
                    }
                  )
              )
            }
          }
        }
    }

  implicit def scribeConstructor[G[_]: Functor, F[_]](
    implicit F: EffectSuspension[F]
  ): LogWriterConstructor[scribe.Logger, G, F] =
    new LogWriterConstructor[scribe.Logger, G, F] {
      val construction: G[scribe.Logger] => G[LogWriter[F]] =
        _ map { scribeLogger =>
          new LogWriter[F] {
            def write[A: Show](level: LogLevel, a: =>A): F[Unit] = {
              val beLevel = level match {
                case LogLevels.Trace => scribe.Level.Trace
                case LogLevels.Debug => scribe.Level.Debug
                case LogLevels.Info  => scribe.Level.Info
                case LogLevels.Warn  => scribe.Level.Warn
                case LogLevels.Error => scribe.Level.Error
              }

              F.suspend(
                a match {
                  case Failure(msg, th) => scribeLogger.log(beLevel, msg, Message.static(th) :: Nil)
                  case _                => scribeLogger.log(beLevel, a.show, Nil)
                }
              )
            }
          }
        }
    }

  implicit def consoleConstructor[LL <: LogLevel, F[_]](
    implicit F: EffectSuspension[F]
  ): LogWriterConstructor[LL, Id, F] =
    new LogWriterConstructor[LL, Id, F] {
      val construction: LL => Id[LogWriter[F]] =
        ll =>
          new LogWriter[F] {
            private val minLogLevel = ll

            def write[A: Show](level: LogLevel, a: =>A): F[Unit] =
              if (level >= minLogLevel)
                F.suspend(
                  println(
                    s"[${level.show.toLowerCase}] - [${Thread.currentThread().getName}] ${a.show}"
                  )
                )
              else F.unit
          }
    }

  implicit val noOpConstructor: LogWriterConstructor[Unit, Id, Id] =
    new LogWriterConstructor[Unit, Id, Id] {
      val construction: Unit => Id[LogWriter[Id]] =
        _ =>
          new LogWriter[Id] {
            def write[A: Show](level: LogLevel, a: =>A): Unit = ()
          }
    }
}
