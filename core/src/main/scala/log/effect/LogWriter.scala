package log.effect

import java.util.{ logging => jul }

import cats.effect.Sync
import cats.{ Applicative, Show }
import com.github.ghik.silencer.silent
import log.effect.LogWriter.{ FailureMessage, LogLevel }
import org.{ log4s => l4s }

import scala.language.implicitConversions

trait LogWriter[F[_]] {
  def write[A: Show](level: LogWriter.LogLevel, a: =>A): F[Unit]
}

object LogWriter extends LogWriterSyntax with LogWriterAliasingSyntax {

  def log4sLog[F[_]: Sync](fa: F[l4s.Logger]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Log4s)
    constructor(fa)
  }

  def log4sLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Log4s)
    constructor(F.delay(l4s.getLogger(c)))
  }

  def log4sLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Log4s)
    constructor(F.delay(l4s.getLogger(n)))
  }

  def julLog[F[_]: Sync](fa: F[jul.Logger]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Jul)
    constructor(fa)
  }

  def consoleLog[F[_]: Sync]: LogWriter[F] = {
    val constructor = LogWriterConstructor0[F](Console)
    constructor()
  }

  def noOpLog[F[_]: Applicative]: LogWriter[F] = {
    val constructor = LogWriterConstructor0[F](NoOp)
    constructor()
  }

  final case object Log4s
  final case object Jul
  final case object Console
  final case object NoOp

  type Log4s   = Log4s.type
  type Jul     = Jul.type
  type Console = Console.type
  type NoOp    = NoOp.type

  sealed trait LogLevel   extends Product with Serializable
  final case object Trace extends LogLevel
  final case object Debug extends LogLevel
  final case object Info  extends LogLevel
  final case object Error extends LogLevel
  final case object Warn  extends LogLevel

  object LogLevel extends LogLevelSyntax {

    implicit private[effect] val logLevelShow: Show[LogLevel] =
      new Show[LogLevel] {
        def show(t: LogLevel): String =
          t match {
            case LogWriter.Trace => "TRACE"
            case LogWriter.Debug => "DEBUG"
            case LogWriter.Info  => "INFO"
            case LogWriter.Warn  => "WARN"
            case LogWriter.Error => "ERROR"
          }
      }
  }

  final private[effect] class FailureMessage(val msg: String, val th: Throwable)

  private[effect] object FailureMessage {

    def apply(msg: String, th: Throwable): FailureMessage =
      new FailureMessage(msg, th)

    def unapply(arg: FailureMessage): Option[(String, Throwable)] =
      Some((arg.msg, arg.th))

    implicit def errorMessageShow: Show[FailureMessage] =
      new Show[FailureMessage] {
        def show(t: FailureMessage): String =
          s"""${t.msg}
             |  Failed with exception ${t.th}
             |  Stack trace:
             |    ${t.th.getStackTrace.toList
               .mkString("\n|    ")}""".stripMargin
      }
  }
}

sealed private[effect] trait LogLevelSyntax {
  implicit def logLevelSyntax(l: LogLevel): LogLevelOps = new LogLevelOps(l)
}

final private[effect] class LogLevelOps(private val l: LogLevel) extends AnyVal {
  def show(implicit ev: Show[LogLevel]): String = ev.show(l)
}

sealed private[effect] trait LogWriterAliasingSyntax {
  @silent implicit def logWriterSingleton[F[_]](co: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): LogWriter[F] = LW
}

sealed private[effect] trait LogWriterSyntax {
  implicit def loggerSyntax[T, F[_]](l: LogWriter[F]): LogWriterOps[F] =
    new LogWriterOps(l)
}

final private[effect] class LogWriterOps[F[_]](private val aLogger: LogWriter[F]) extends AnyVal {

  import cats.instances.string._

  @inline def trace(msg: =>String): F[Unit] =
    aLogger.write(LogWriter.Trace, msg)

  @inline def trace(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(LogWriter.Trace, FailureMessage(msg, th))

  @inline def debug(msg: =>String): F[Unit] =
    aLogger.write(LogWriter.Debug, msg)

  @inline def debug(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(LogWriter.Debug, FailureMessage(msg, th))

  @inline def info(msg: =>String): F[Unit] =
    aLogger.write(LogWriter.Info, msg)

  @inline def info(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(LogWriter.Info, FailureMessage(msg, th))

  @inline def error(msg: =>String): F[Unit] =
    aLogger.write(LogWriter.Error, msg)

  @inline def error(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(LogWriter.Error, FailureMessage(msg, th))

  @inline def warn(msg: =>String): F[Unit] =
    aLogger.write(LogWriter.Warn, msg)

  @inline def warn(msg: =>String, th: =>Throwable): F[Unit] =
    aLogger.write(LogWriter.Warn, FailureMessage(msg, th))
}
