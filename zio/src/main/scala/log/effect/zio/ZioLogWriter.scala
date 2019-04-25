package log
package effect
package zio

import java.util.{ logging => jul }

import log.effect.internal.{ EffectSuspension, Id }
import org.{ log4s => l4s }
import scalaz.zio.{ IO, Task, UIO, ZIO }

object ZioLogWriter {

  import instances._

  val log4sFromLogger: ZIO[l4s.Logger, Nothing, LogWriter[Task]] =
    ZIO.accessM { log4sLogger =>
      LogWriter.from[UIO].runningEffect[Task](ZIO.effectTotal(log4sLogger))
    }

  val log4sFromName: ZIO[String, Throwable, LogWriter[Task]] =
    ZIO.accessM { name =>
      LogWriter.of[Task](ZIO.effect(l4s.getLogger(name)))
    }

  val log4sFromClass: ZIO[Class[_], Throwable, LogWriter[Task]] =
    ZIO.accessM { c =>
      LogWriter.of[Task](ZIO.effect(l4s.getLogger(c)))
    }

  val julFromLogger: ZIO[jul.Logger, Nothing, LogWriter[Task]] =
    ZIO.accessM { julLogger =>
      LogWriter.from[UIO].runningEffect[Task](ZIO.effectTotal(julLogger))
    }

  val julGlobal: ZIO[Any, Throwable, LogWriter[Task]] =
    LogWriter.of[Task](IO.effect(jul.Logger.getGlobal))

  val scribeFromName: ZIO[String, Throwable, LogWriter[Task]] =
    ZIO.accessM { name =>
      LogWriter.of[Task](IO.effect(scribe.Logger(name)))
    }

  val scribeFromClass: ZIO[Class[_], Throwable, LogWriter[Task]] =
    ZIO.accessM { c =>
      import scribe._
      LogWriter.of[Task](IO.effect(c.logger))
    }

  val scribeFromLogger: ZIO[scribe.Logger, Nothing, LogWriter[Task]] =
    ZIO.accessM { scribeLogger =>
      LogWriter.from[UIO].runningEffect[Task](ZIO.effectTotal(scribeLogger))
    }

  val console: LogWriter[Task] =
    LogWriter.from[Id].runningEffect[Task](LogLevels.Trace)

  def consoleUpToLevel[LL <: LogLevel](minLevel: LL): LogWriter[Task] =
    LogWriter.from[Id].runningEffect[Task](minLevel)

  val noOp: LogWriter[Id] =
    LogWriter.of[Id](())

  private[this] object instances {

    implicit final private[zio] val taskEffectSuspension: EffectSuspension[Task] =
      new EffectSuspension[Task] {
        def suspend[A](a: =>A): Task[A] = IO.effect(a)
      }

    implicit final private[zio] val uioEffectSuspension: EffectSuspension[UIO] =
      new EffectSuspension[UIO] {
        def suspend[A](a: =>A): UIO[A] = IO.effectTotal(a)
      }

    implicit final private[zio] def functorInstances[R, E]: internal.Functor[ZIO[R, E, ?]] =
      new internal.Functor[ZIO[R, E, ?]] {
        def fmap[A, B](f: A => B): ZIO[R, E, A] => ZIO[R, E, B] = _ map f
      }
  }
}
