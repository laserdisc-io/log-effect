package log.effect
package interop

import io.chrisdavenport.log4cats.{Logger, MessageLogger}
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
