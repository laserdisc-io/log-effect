package log
package effect

import java.util.{ logging => jul }

import log.effect.internal._
import log.effect.internal.syntax._
import org.{ log4s => l4s }

sealed trait LogWriterConstructor[R, G[_], F[_]] {
  def construction: R => G[LogWriter[F]]
}

object LogWriterConstructor {
  implicit def log4sConstructor[G[_]: Functor, F[_]](
    implicit F: EffectSuspension[F]
  ): LogWriterConstructor[G[l4s.Logger], G, F] =
    new LogWriterConstructor[G[l4s.Logger], G, F] {
      val construction: G[l4s.Logger] => G[LogWriter[F]] =
        _ map { l4sLogger =>
          new LogWriter[F] {
            def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit] = {
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
  ): LogWriterConstructor[G[jul.Logger], G, F] =
    new LogWriterConstructor[G[jul.Logger], G, F] {
      val construction: G[jul.Logger] => G[LogWriter[F]] =
        _ map { julLogger =>
          new LogWriter[F] {
            def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit] = {
              val beLevel = level match {
                case LogLevels.Trace => jul.Level.FINEST
                case LogLevels.Debug => jul.Level.FINE
                case LogLevels.Info  => jul.Level.INFO
                case LogLevels.Warn  => jul.Level.WARNING
                case LogLevels.Error => jul.Level.SEVERE
              }

              F.suspend(
                if (julLogger.isLoggable(beLevel)) {
                  julLogger.log(
                    a match {
                      case Failure(msg, th) =>
                        val r = new jul.LogRecord(beLevel, msg)
                        r.setThrown(th)
                        r
                      case _ => new jul.LogRecord(beLevel, a.show)
                    }
                  )
                }
              )
            }
          }
        }
    }

  implicit def scribeConstructor[G[_]: Functor, F[_]](
    implicit F: EffectSuspension[F]
  ): LogWriterConstructor[G[scribe.Logger], G, F] =
    new LogWriterConstructor[G[scribe.Logger], G, F] {
      val construction: G[scribe.Logger] => G[LogWriter[F]] =
        _ map { scribeLogger =>
          new LogWriter[F] {
            def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit] = {
              val beLevel = level match {
                case LogLevels.Trace => scribe.Level.Trace
                case LogLevels.Debug => scribe.Level.Debug
                case LogLevels.Info  => scribe.Level.Info
                case LogLevels.Warn  => scribe.Level.Warn
                case LogLevels.Error => scribe.Level.Error
              }

              F.suspend(
                a match {
                  case Failure(msg, th) => scribeLogger.log(beLevel, msg, Some(th))
                  case _                => scribeLogger.log(beLevel, a.show, None)
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

            def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit] =
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
            def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): Unit = ()
          }
    }
}
