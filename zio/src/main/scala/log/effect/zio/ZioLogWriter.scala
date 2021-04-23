package log.effect
package zio

import java.util.{logging => jul}

import _root_.zio._
import com.github.ghik.silencer.silent
import log.effect.internal.{EffectSuspension, Id, Show}
import org.{log4s => l4s}

object ZioLogWriter {
  import instances._

  final case class LogName(x: String) extends AnyVal

  final type ZLogName      = Has[LogName]
  final type ZLogClass[A]  = Has[Class[A]]
  final type ZLog4sLogger  = Has[l4s.Logger]
  final type ZJulLogger    = Has[jul.Logger]
  final type ZScribeLogger = Has[scribe.Logger]
  final type ZLogWriter    = Has[LogWriter[Task]]

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

  // Needed for 2.12.12 where these warnings still appear
  @silent("a type was inferred to be `Any`; this may indicate a programming error.")
  val log4sLayerFromName: RLayer[ZLogName, ZLogWriter] =
    ZLayer.fromServiceM(name => log4sFromName provide name.x)

  @silent("a type was inferred to be `Any`; this may indicate a programming error.")
  val log4sLayerFromLogger: RLayer[ZLog4sLogger, ZLogWriter] =
    ZLayer.fromServiceM(log4sFromLogger.provide)

  @silent("a type was inferred to be `Any`; this may indicate a programming error.")
  val julLayerFromLogger: RLayer[ZJulLogger, ZLogWriter] =
    ZLayer.fromServiceM(julFromLogger.provide)

  @silent("a type was inferred to be `Any`; this may indicate a programming error.")
  val scribeLayerFromName: RLayer[ZLogName, ZLogWriter] =
    ZLayer.fromServiceM(name => scribeFromName provide name.x)

  @silent("a type was inferred to be `Any`; this may indicate a programming error.")
  val scribeLayerFromLogger: RLayer[ZScribeLogger, ZLogWriter] =
    ZLayer.fromServiceM(scribeFromLogger.provide)

  val consoleLogLayer: ULayer[ZLogWriter] =
    ZLayer.succeed(consoleLog)

  def consoleLogLayerUpToLevel[LL <: LogLevel](minLevel: LL): ULayer[ZLogWriter] =
    ZLayer.succeed(consoleLogUpToLevel(minLevel))

  val noOpLogLayer: ULayer[ZLogWriter] =
    ZLayer.succeed(noOpLog)

  private[this] object instances {
    private[zio] implicit final val taskEffectSuspension: EffectSuspension[Task] =
      new EffectSuspension[Task] {
        def suspend[A](a: =>A): Task[A] = IO.effect(a)
      }

    private[zio] implicit final val uioEffectSuspension: EffectSuspension[UIO] =
      new EffectSuspension[UIO] {
        def suspend[A](a: =>A): UIO[A] = IO.effectTotal(a)
      }

    private[zio] implicit final def functorInstances[
      R,
      E
    ]: log.effect.internal.Functor[ZIO[R, E, *]] =
      new log.effect.internal.Functor[ZIO[R, E, *]] {
        def fmap[A, B](f: A => B): ZIO[R, E, A] => ZIO[R, E, B] = _ map f
      }

    implicit final class NoOpLogT(private val `_`: LogWriter[Id]) extends AnyVal {
      def liftT: LogWriter[Task] =
        new LogWriter[Task] {
          override def write[A: Show](level: LogLevel, a: =>A): Task[Unit] = Task.unit
        }
    }
  }
}
