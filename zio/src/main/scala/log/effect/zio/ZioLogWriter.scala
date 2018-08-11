package log.effect
package zio

import java.io.IOException

import log.effect.internal.EffectSuspension
import scalaz.zio.IO

object ZioLogWriter {

  type ExceptionZIO[A]   = IO[Exception, A]
  type IOExceptionZIO[A] = IO[IOException, A]
  type NonFailingZIO[A]  = IO[Nothing, A]

  def log4sLogZio(c: Class[_]): ExceptionZIO[LogWriter[ExceptionZIO]] = ???

  def log4sLogZio[F[_]](n: String): ExceptionZIO[LogWriter[ExceptionZIO]] = ???

  def julLogZio[F[_]]: ExceptionZIO[LogWriter[ExceptionZIO]] = ???

  def consoleLogZio: NonFailingZIO[LogWriter[IOExceptionZIO]] = ???

  def consoleLogZioUpToLevel: ConsoleLogZioPartial =
    new ConsoleLogZioPartial

  final private[zio] class ConsoleLogZioPartial(private val d: Boolean = true) extends AnyVal {
    def apply[LL <: LogLevel](minLevel: LL): NonFailingZIO[LogWriter[IOExceptionZIO]] = ???
  }

  def noOpLogZio: NonFailingZIO[LogWriter[NonFailingZIO]] = ???

  implicit final private def instance1: EffectSuspension[ExceptionZIO] =
    new EffectSuspension[ExceptionZIO] {
      def suspend[A](a: =>A): ExceptionZIO[A] = IO.sync(a)
    }
}
