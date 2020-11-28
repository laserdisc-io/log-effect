package log.effect

import com.github.ghik.silencer.silent
import log.effect.internal.Show

import scala.language.implicitConversions

sealed trait LogLevel extends Product with Serializable
object LogLevel extends LogLevelSyntax {
  import LogLevels._

  implicit val logLevelShow: Show[LogLevel] = {
    case l: Trace => showFor(l).show(l)
    case l: Debug => showFor(l).show(l)
    case l: Info  => showFor(l).show(l)
    case l: Warn  => showFor(l).show(l)
    case l: Error => showFor(l).show(l)
  }

  implicit val logLevelOrdering: Ordering[LogLevel] =
    (x: LogLevel, y: LogLevel) =>
      x match {
        case Trace =>
          y match {
            case Trace                       => 0
            case Debug | Info | Warn | Error => -1
          }

        case Debug =>
          y match {
            case Trace               => 1
            case Debug               => 0
            case Info | Warn | Error => -1
          }

        case Info =>
          y match {
            case Trace | Debug => 1
            case Info          => 0
            case Warn | Error  => -1
          }

        case Warn =>
          y match {
            case Trace | Debug | Info => 1
            case Warn                 => 0
            case Error                => -1
          }

        case Error =>
          y match {
            case Trace | Debug | Info | Warn => 1
            case Error                       => 0
          }
      }

  @silent private def showFor[A](a: A)(implicit ev: Show[A]): Show[A] = ev
}

sealed trait LogLevelSyntax {
  implicit def logLevelSyntax[L <: LogLevel](l: L): LogLevelOps[L] = new LogLevelOps(l)
}

private[effect] final class LogLevelOps[L <: LogLevel](private val l: L) extends AnyVal {
  def show(implicit ev: Show[LogLevel]): String = ev.show(l)

  def >=[LL <: LogLevel](other: LL)(implicit ord: Ordering[LogLevel]): Boolean =
    ord.gteq(l, other)
}

object LogLevels {
  type Trace = Trace.type
  type Debug = Debug.type
  type Info  = Info.type
  type Warn  = Warn.type
  type Error = Error.type

  case object Trace extends LogLevel {
    implicit val traceShow: Show[Trace] =
      (_: Trace) => "TRACE"
  }

  case object Debug extends LogLevel {
    implicit val debugShow: Show[Debug] =
      (_: Debug) => "DEBUG"
  }

  case object Info extends LogLevel {
    implicit val infoShow: Show[Info] =
      (_: Info) => "INFO"
  }

  case object Warn extends LogLevel {
    implicit val warnShow: Show[Warn] =
      (_: Warn) => "WARN"
  }

  case object Error extends LogLevel {
    implicit val errorShow: Show[Error] =
      (_: Error) => "ERROR"
  }
}
