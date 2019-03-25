package log
package effect
package zio

import java.io.IOException
import java.util.{ logging => jul }

import log.effect.LogWriter.{ Console, Jul, Log4s, NoOp, Scribe }
import log.effect.internal.{ EffectSuspension, NoAction }
import org.{ log4s => l4s }
import scalaz.zio.{ IO, Task }

object ZioLogWriter {

  def log4sLogZio(fa: Task[l4s.Logger]): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Log4s)
    constructor(fa)
  }

  def log4sLogZio(c: Class[_]): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Log4s)
    constructor(IO.effect(l4s.getLogger(c)))
  }

  def log4sLogZio(n: String): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Log4s)
    constructor(IO.effect(l4s.getLogger(n)))
  }

  def julLogZio(fa: Task[jul.Logger]): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Jul)
    constructor(fa)
  }

  def julLogZio[F[_]]: Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Jul)
    constructor(IO.effect(jul.Logger.getGlobal))
  }

  def scribeLogZio(fa: Task[scribe.Logger]): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Scribe)
    constructor(fa)
  }

  def scribeLogZio(c: Class[_])(
    implicit ev: Class[_] <:< scribe.Logger
  ): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Scribe)
    constructor(IO.effect(c))
  }

  def scribeLogZio(n: String): Task[LogWriter[Task]] = {
    val constructor = LogWriterConstructor1[Task](Scribe)
    constructor(IO.effect(scribe.Logger(n)))
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
      def suspend[A](a: =>A): IO[E, A] = IO.suspend(IO.succeed(a))
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
