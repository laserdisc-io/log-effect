package log
package effect
package zio

import java.util.{logging => jul}

import _root_.zio.{IO, RIO, Task, UIO, URIO, ZIO}
import log.effect.internal.{EffectSuspension, Id, Show}
import org.{log4s => l4s}

object ZioLogWriter {
  import instances._

  val log4sFromLogger: URIO[l4s.Logger, LogWriter[Task]] =
    ZIO.access(log4sLogger => LogWriter.pureOf[Task](log4sLogger))

  val log4sFromName: RIO[String, LogWriter[Task]] =
    ZIO.accessM(name => LogWriter.of[Task](ZIO.effect(l4s.getLogger(name))))

  val log4sFromClass: RIO[Class[_], LogWriter[Task]] =
    ZIO.accessM(c => LogWriter.of[Task](ZIO.effect(l4s.getLogger(c))))

  val julFromLogger: URIO[jul.Logger, LogWriter[Task]] =
    ZIO.access(julLogger => LogWriter.pureOf[Task](julLogger))

  val julGlobal: Task[LogWriter[Task]] =
    LogWriter.of[Task](IO.effect(jul.Logger.getGlobal))

  val scribeFromName: RIO[String, LogWriter[Task]] =
    ZIO.accessM(name => LogWriter.of[Task](IO.effect(scribe.Logger(name))))

  val scribeFromClass: RIO[Class[_], LogWriter[Task]] =
    ZIO.accessM { c =>
      import scribe._
      LogWriter.of[Task](IO.effect(c.logger))
    }

  val scribeFromLogger: URIO[scribe.Logger, LogWriter[Task]] =
    ZIO.access(scribeLogger => LogWriter.pureOf(scribeLogger))

  val consoleLog: LogWriter[Task] =
    LogWriter.pureOf[Task](LogLevels.Trace)

  def consoleLogUpToLevel[LL <: LogLevel](minLevel: LL): LogWriter[Task] =
    LogWriter.pureOf[Task](minLevel)

  val noOpLog: LogWriter[Task] =
    LogWriter.of[Id](()).liftT

  private[this] object instances {
    private[zio] implicit final val taskEffectSuspension: EffectSuspension[Task] =
      new EffectSuspension[Task] {
        def suspend[A](a: =>A): Task[A] = IO.effect(a)
      }

    private[zio] implicit final val uioEffectSuspension: EffectSuspension[UIO] =
      new EffectSuspension[UIO] {
        def suspend[A](a: =>A): UIO[A] = IO.effectTotal(a)
      }

    private[zio] implicit final def functorInstances[R, E]: internal.Functor[ZIO[R, E, *]] =
      new internal.Functor[ZIO[R, E, *]] {
        def fmap[A, B](f: A => B): ZIO[R, E, A] => ZIO[R, E, B] = _ map f
      }

    implicit final class NoOpLogT(private val `_`: LogWriter[Id]) extends AnyVal {
      def liftT: LogWriter[Task] =
        new LogWriter[Task] {
          override def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): Task[Unit] = Task.unit
        }
    }
  }
}
