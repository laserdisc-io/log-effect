package log.effect
package zio

import java.io.IOException
import java.util.{ logging => jul }

import log.effect.LogWriter.{ Console, Jul, Log4s, NoOp, Scribe }
import log.effect.internal.{ EffectSuspension, NoAction }
import org.{ log4s => l4s }
import scalaz.zio.IO

object ZioLogWriter {

  type ExIO[A] = IO[Exception, A]

  def log4sLogZio(fa: ExIO[l4s.Logger]): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Log4s)
    constructor(fa)
  }

  def log4sLogZio(c: Class[_]): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Log4s)
    constructor(IO.sync(l4s.getLogger(c)))
  }

  def log4sLogZio(n: String): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Log4s)
    constructor(IO.sync(l4s.getLogger(n)))
  }

  def julLogZio(fa: ExIO[jul.Logger]): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Jul)
    constructor(fa)
  }

  def julLogZio[F[_]]: ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Jul)
    constructor(IO.sync(jul.Logger.getGlobal))
  }

  def scribeLogZio(fa: ExIO[scribe.Logger]): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Scribe)
    constructor(fa)
  }

  def scribeLogZio(c: Class[_])(
    implicit ev: Class[_] <:< scribe.Logger
  ): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Scribe)
    constructor(IO.sync(c))
  }

  def scribeLogZio(n: String): ExIO[LogWriter[ExIO]] = {
    val constructor = LogWriterConstructor1[ExIO](Scribe)
    constructor(IO.sync(scribe.Logger(n)))
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

  implicit final private def functorInstances[E]: internal.Functor[IO[E, ?]] =
    new internal.Functor[IO[E, ?]] {
      def fmap[A, B](f: A => B): IO[E, A] => IO[E, B] = _ map f
    }

  implicit final private def noActionInstances[E]: NoAction[IO[E, ?]] =
    new NoAction[IO[E, ?]] {
      def unit: IO[E, Unit] = IO.unit
    }
}
