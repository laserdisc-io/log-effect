package log.effect

import java.util.{logging => jul}

import cats.Show
import cats.effect.Sync
import com.github.ghik.silencer.silent
import log.effect.LogWriter.{FailureMessage, LogLevel}
import org.{log4s => l4s}

import scala.language.implicitConversions

trait LogWriter[F[_]] {
  def write[A: Show](level: LogWriter.LogLevel, a: =>A): F[Unit]
}

object LogWriter extends LogWriterSyntax with LogWriterAliasingSyntax {

  def log4sLog[F[_] : Sync](fa: F[l4s.Logger]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor[F](Log4s)
    constructor(fa)
  }

  def julLog[F[_] : Sync](fa: F[jul.Logger]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor[F](Jul)
    constructor(fa)
  }

  def consoleLog[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor[F](Console)
    constructor(F.unit)
  }

  final case object Log4s
  final case object Jul
  final case object Console

  type Log4s   = Log4s.type
  type Jul     = Jul.type
  type Console = Console.type

  sealed trait LogLevel extends Product with Serializable
  final case object Trace extends LogLevel
  final case object Debug extends LogLevel
  final case object Info  extends LogLevel
  final case object Error extends LogLevel
  final case object Warn  extends LogLevel

  object LogLevel extends LogLevelSyntax {

    implicit val logLevelShow: Show[LogLevel] =
      new Show[LogLevel] {
        def show(t: LogLevel): String =
          t match {
            case LogWriter.Trace  => "TRACE"
            case LogWriter.Debug  => "DEBUG"
            case LogWriter.Info   => "INFO"
            case LogWriter.Warn   => "WARN"
            case LogWriter.Error  => "ERROR"
          }
      }
  }

  private[effect] final class FailureMessage(val msg: String, val th: Throwable)
  private[effect] object FailureMessage {

    def apply(msg: String, th: Throwable): FailureMessage = new FailureMessage(msg, th)
    def unapply(arg: FailureMessage): Option[(String, Throwable)] = Some((arg.msg, arg.th))

    implicit def errorMessageShow: Show[FailureMessage] =
      new Show[FailureMessage] {
        def show(t: FailureMessage): String =
          s"""${t.msg}.
             |
             |Failed with exception ${t.th} - ${t.th.getMessage}
             |
             |${t.th.printStackTrace()}
           """.stripMargin
      }
  }
}

private[effect] sealed trait LogLevelSyntax {
  implicit def logLevelSyntax(l: LogLevel): LogLevelOps = new LogLevelOps(l)
}

private[effect] final class LogLevelOps(private val l: LogLevel) extends AnyVal {
  def show(implicit ev: Show[LogLevel]): String = ev.show(l)
}

private[effect] sealed trait LogWriterAliasingSyntax {
  @silent implicit def logWriterSingleton[F[_]](co: LogWriter.type)(implicit LW: LogWriter[F]): LogWriter[F] = LW
}

private[effect] sealed trait LogWriterSyntax {
  implicit def loggerSyntax[T, F[_]](l: LogWriter[F]): LogWriterOps[F] = new LogWriterOps(l)
}

private[effect] final class LogWriterOps[F[_]](private val aLogger: LogWriter[F]) extends AnyVal {

  import cats.instances.string._

  @inline def trace(msg: =>String): F[Unit]                   = aLogger.write(LogWriter.Trace, msg)
  @inline def trace(msg: =>String, th: =>Throwable): F[Unit]  = aLogger.write(LogWriter.Trace, FailureMessage(msg, th))

  @inline def debug(msg: =>String): F[Unit]                   = aLogger.write(LogWriter.Debug, msg)
  @inline def debug(msg: =>String, th: =>Throwable): F[Unit]  = aLogger.write(LogWriter.Debug, FailureMessage(msg, th))

  @inline def info(msg: =>String): F[Unit]                  = aLogger.write(LogWriter.Info, msg)
  @inline def info(msg: =>String, th: =>Throwable): F[Unit] = aLogger.write(LogWriter.Info, FailureMessage(msg, th))

  @inline def error(msg: =>String): F[Unit]                   = aLogger.write(LogWriter.Error, msg)
  @inline def error(msg: =>String, th: =>Throwable): F[Unit]  = aLogger.write(LogWriter.Error, FailureMessage(msg, th))

  @inline def warn(msg: =>String): F[Unit]                  = aLogger.write(LogWriter.Warn, msg)
  @inline def warn(msg: =>String, th: =>Throwable): F[Unit] = aLogger.write(LogWriter.Warn, FailureMessage(msg, th))
}

//object LogWriterResolutionTest {
//
//  trait TestLog[F[_]] {
//
//    implicit val F: Sync[F]
//
//    val log4sConst    = LogWriterConstructor[F](Log4s)
//    val julConst      = LogWriterConstructor[F](Jul)
//    val consoleConst  = LogWriterConstructor[F](Console)
//
//    val logger1: F[LogWriter[F]] = log4sConst(F.delay(getLogger("test")))
//    val logger2: F[LogWriter[F]] = julConst(F.delay(jul.Logger.getGlobal))
//    val logger3: F[LogWriter[F]] = consoleConst(F.unit)
//  }
//
//  val resolvedLogs = new TestLog[IO] { val F: Sync[IO] = Sync[IO] }
//
//  val log4sConst1 = LogWriterConstructor[IO](Log4s)
//  val logger4: IO[LogWriter[IO]] = log4sConst1(IO(getLogger("test")))
//
//  val julConst1 = LogWriterConstructor[IO](Jul)
//  val logger5: IO[LogWriter[IO]] = julConst1(IO(jul.Logger.getGlobal))
//
//  val consoleConst1 = LogWriterConstructor[IO](Console)
//  val logger6: IO[LogWriter[IO]] = consoleConst1(IO.unit)
//}