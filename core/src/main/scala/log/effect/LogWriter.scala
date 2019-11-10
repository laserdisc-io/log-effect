package log
package effect

import com.github.ghik.silencer.silent
import log.effect.internal.{ Id, Show }

import scala.language.implicitConversions

trait LogWriter[F[_]] {
  def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit]
}

object LogWriter extends LogWriterSyntax {
  @inline final def pureOf[F[_]]: logWriterInstancePartial[Id, F] =
    new logWriterInstancePartial[Id, F]()

  @inline final def of[F[_]]: logWriterInstancePartial[F, F] =
    new logWriterInstancePartial()

  final private[effect] class logWriterInstancePartial[G[_], F[_]](private val d: Boolean = true)
      extends AnyVal {
    @inline @silent def apply[R](read: G[R])(
      implicit instance: LogWriterConstructor[R, G, F]
    ): G[LogWriter[F]] =
      instance construction read
  }
}

sealed private[effect] trait LogWriterSyntax extends LogWriterAliasingSyntax {
  implicit def loggerSyntax[T, F[_]](l: LogWriter[F]): LogWriterOps[F] =
    new LogWriterOps(l)
}

sealed private[effect] trait LogWriterAliasingSyntax {
  @silent implicit def logWriterSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriter[F] = LW

  @silent implicit def logWriterOpsSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriterOps[F] = new LogWriterOps(LW)
}

final private[effect] class LogWriterOps[F[_]](private val aLogger: LogWriter[F]) extends AnyVal {
  import LogLevels._

  @inline def trace[A: Show](a: =>A): F[Unit] =
    aLogger.write(Trace, a)

  @inline def trace(msg: =>String): F[Unit] =
    aLogger.write(Trace, msg)

  @inline def trace(th: =>Throwable)(implicit `_`: DummyImplicit): F[Unit] =
    aLogger.write(Trace, th)

  @inline def trace(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Trace, Failure(msg, th))

  @inline def debug[A: Show](a: =>A): F[Unit] =
    aLogger.write(Debug, a)

  @inline def debug(msg: =>String): F[Unit] =
    aLogger.write(Debug, msg)

  @inline def debug(th: =>Throwable)(implicit `_`: DummyImplicit): F[Unit] =
    aLogger.write(Debug, th)

  @inline def debug(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Debug, Failure(msg, th))

  @inline def info[A: Show](a: =>A): F[Unit] =
    aLogger.write(Info, a)

  @inline def info(msg: =>String): F[Unit] =
    aLogger.write(Info, msg)

  @inline def info(th: =>Throwable)(implicit `_`: DummyImplicit): F[Unit] =
    aLogger.write(Info, th)

  @inline def info(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Info, Failure(msg, th))

  @inline def error[A: Show](a: =>A): F[Unit] =
    aLogger.write(Error, a)

  @inline def error(msg: =>String): F[Unit] =
    aLogger.write(Error, msg)

  @inline def error(th: =>Throwable)(implicit `_`: DummyImplicit): F[Unit] =
    aLogger.write(Error, th)

  @inline def error(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Error, Failure(msg, th))

  @inline def warn[A: Show](a: =>A): F[Unit] =
    aLogger.write(Warn, a)

  @inline def warn(msg: =>String): F[Unit] =
    aLogger.write(Warn, msg)

  @inline def warn(th: =>Throwable)(implicit `_`: DummyImplicit): F[Unit] =
    aLogger.write(Warn, th)

  @inline def warn(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Warn, Failure(msg, th))
}
