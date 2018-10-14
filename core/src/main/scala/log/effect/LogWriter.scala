package log.effect

import com.github.ghik.silencer.silent
import log.effect.LogWriter.Failure
import log.effect.internal.Show

import scala.language.implicitConversions

trait LogWriter[F[_]] {
  def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit]
}

object LogWriter extends LogWriterSyntax with LogWriterAliasingSyntax {

  final case object Log4s
  final case object Jul
  final case object Scribe
  final case object Console
  final case object NoOp

  final type Log4s   = Log4s.type
  final type Jul     = Jul.type
  final type Scribe  = Scribe.type
  final type Console = Console.type
  final type NoOp    = NoOp.type

  final class Failure(val msg: String, val th: Throwable)

  object Failure {

    def apply(msg: String, th: Throwable): Failure =
      new Failure(msg, th)

    def unapply(arg: Failure): Option[(String, Throwable)] =
      Some((arg.msg, arg.th))

    implicit def failureShow: Show[Failure] =
      new Show[Failure] {
        def show(t: Failure): String =
          s"""${t.msg}
           |  Failed with exception ${t.th}
           |  Stack trace:
           |    ${t.th.getStackTrace.toList
               .mkString("\n|    ")}""".stripMargin
      }
  }
}

sealed private[effect] trait LogWriterAliasingSyntax {
  @silent implicit def logWriterSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriter[F] = LW

  @silent implicit def logWriterOpsSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriterOps[F] = new LogWriterOps(LW)
}

sealed private[effect] trait LogWriterSyntax {
  implicit def loggerSyntax[T, F[_]](l: LogWriter[F]): LogWriterOps[F] =
    new LogWriterOps(l)
}

final private[effect] class LogWriterOps[F[_]](private val aLogger: LogWriter[F]) extends AnyVal {

  import LogLevels._

  @inline def trace(msg: =>String): F[Unit] =
    aLogger.write(Trace, msg)

  @inline def trace(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Trace, Failure(msg, th))

  @inline def debug(msg: =>String): F[Unit] =
    aLogger.write(Debug, msg)

  @inline def debug(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Debug, Failure(msg, th))

  @inline def info(msg: =>String): F[Unit] =
    aLogger.write(Info, msg)

  @inline def info(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Info, Failure(msg, th))

  @inline def error(msg: =>String): F[Unit] =
    aLogger.write(Error, msg)

  @inline def error(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Error, Failure(msg, th))

  @inline def warn(msg: =>String): F[Unit] =
    aLogger.write(Warn, msg)

  @inline def warn(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(Warn, Failure(msg, th))
}
