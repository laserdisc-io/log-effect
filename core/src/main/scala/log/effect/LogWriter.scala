package log.effect

import java.util.{ logging => jul }

import cats.effect.Sync
import cats.{ Applicative, Show }
import com.github.ghik.silencer.silent
import log.effect.LogWriter.Failure
import org.{ log4s => l4s }

import scala.language.implicitConversions

trait LogWriter[F[_]] {
  def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit]
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

  def consoleLogUpToLevel[F[_]]: ConsoleLogPartial[F] =
    new ConsoleLogPartial[F]

  final private[effect] class ConsoleLogPartial[F[_]](private val d: Boolean = true)
      extends AnyVal {
    def apply[LL <: LogLevel](minLevel: LL)(implicit F: Sync[F]): LogWriter[F] = {
      val constructor = LogWriterConstructor0[F](Console, minLevel)
      constructor()
    }
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
}

sealed private[effect] trait LogWriterSyntax {
  implicit def loggerSyntax[T, F[_]](l: LogWriter[F]): LogWriterOps[F] =
    new LogWriterOps(l)
}

final private[effect] class LogWriterOps[F[_]](private val aLogger: LogWriter[F]) extends AnyVal {

  import LogLevels._
  import cats.instances.string._

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
