package log.effect
package zio

import java.io.IOException
import java.util.{ logging => jul }

import cats.Applicative
import log.effect.LogWriter.{ Console, Jul, Log4s, NoOp }
import log.effect.internal.EffectSuspension
import org.{ log4s => l4s }
import scalaz.zio.IO

object ZioLogWriter {

  type ExceptionZIO[A] = IO[Exception, A]

  def log4sLogZio(fa: ExceptionZIO[l4s.Logger]): ExceptionZIO[LogWriter[ExceptionZIO]] = {
    val constructor = LogWriterConstructor1[ExceptionZIO](Log4s)
    constructor(fa)
  }

  def log4sLogZio(c: Class[_]): ExceptionZIO[LogWriter[ExceptionZIO]] = {
    val constructor = LogWriterConstructor1[ExceptionZIO](Log4s)
    constructor(IO.sync(l4s.getLogger(c)))
  }

  def log4sLogZio[F[_]](n: String): ExceptionZIO[LogWriter[ExceptionZIO]] = {
    val constructor = LogWriterConstructor1[ExceptionZIO](Log4s)
    constructor(IO.sync(l4s.getLogger(n)))
  }

  def julLogZio[F[_]](fa: ExceptionZIO[jul.Logger]): ExceptionZIO[LogWriter[ExceptionZIO]] = {
    val constructor = LogWriterConstructor1[ExceptionZIO](Jul)
    constructor(fa)
  }

  def consoleLogZio: LogWriter[IO[IOException, ?]] = {
    val constructor = LogWriterConstructor0[IO[IOException, ?]](Console)
    constructor()
  }

  def consoleLogZioUpToLevel: ConsoleLogZioPartial =
    new ConsoleLogZioPartial

  final private[zio] class ConsoleLogZioPartial(private val d: Boolean = true) extends AnyVal {
    def apply[LL <: LogLevel](minLevel: LL): LogWriter[IO[IOException, ?]] = {
      val constructor = LogWriterConstructor0[IO[IOException, ?]](Console, minLevel)
      constructor()
    }
  }

  def noOpLogZio: LogWriter[IO[Nothing, ?]] = {
    val constructor = LogWriterConstructor0[IO[Nothing, ?]](NoOp)
    constructor()
  }

  implicit final private def instance[E]: EffectSuspension[IO[E, ?]] =
    new EffectSuspension[IO[E, ?]] {
      def suspend[A](a: =>A): IO[E, A] = IO.sync(a)
    }

  implicit final private def zioApplicative[E]: Applicative[IO[E, ?]] =
    new Applicative[IO[E, ?]] {
      def pure[A](x: A): IO[E, A] = IO.now(x)
      def ap[A, B](ff: IO[E, A => B])(fa: IO[E, A]): IO[E, B] =
        fa flatMap (a => ff map (f => f(a)))
    }
}
