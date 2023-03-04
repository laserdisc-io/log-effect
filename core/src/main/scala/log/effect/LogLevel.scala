/*
 * Copyright (c) 2023 LaserDisc
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

import log.effect.internal.Show

import scala.annotation.nowarn

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

  @nowarn private def showFor[A](a: A)(implicit ev: Show[A]): Show[A] = ev
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
