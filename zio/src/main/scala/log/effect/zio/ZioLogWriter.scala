/*
 * Copyright (c) 2024 LaserDisc
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
package zio

import _root_.zio.{LogLevel => _, _}
import log.effect.internal.{EffectSuspension, Id, Show}
import org.{log4s => l4s}

import java.util.{logging => jul}

object ZioLogWriter {
  import instances._

  final case class LogName(x: String) extends AnyVal

  final type ZLogName      = LogName
  final type ZLogClass[A]  = Class[A]
  final type ZLog4sLogger  = l4s.Logger
  final type ZJulLogger    = jul.Logger
  final type ZScribeLogger = scribe.Logger
  final type ZLogWriter    = LogWriter[Task]

  val log4sFromLogger: URIO[ZLog4sLogger, ZLogWriter] =
    ZIO.serviceWith(log4sLogger => LogWriter.pureOf[Task](log4sLogger))

  val log4sFromName: RIO[String, ZLogWriter] =
    ZIO.serviceWithZIO(name => LogWriter.of[Task](ZIO.attempt(l4s.getLogger(name))))

  val log4sFromClass: RIO[Class[_], ZLogWriter] =
    ZIO.serviceWithZIO((clazz: Class[_]) => LogWriter.of[Task](ZIO.attempt(l4s.getLogger(clazz))))

  val julFromLogger: URIO[ZJulLogger, ZLogWriter] =
    ZIO.serviceWith(julLogger => LogWriter.pureOf[Task](julLogger))

  val julGlobal: Task[ZLogWriter] =
    LogWriter.of[Task](ZIO.attempt(jul.Logger.getGlobal))

  val scribeFromName: RIO[String, ZLogWriter] =
    ZIO.serviceWithZIO(name => LogWriter.of[Task](ZIO.attempt(scribe.Logger(name))))

  val scribeFromClass: RIO[Class[_], ZLogWriter] =
    ZIO.serviceWithZIO { (clazz: Class[_]) =>
      import scribe._
      LogWriter.of[Task](ZIO.attempt(clazz.logger))
    }

  val scribeFromLogger: URIO[ZScribeLogger, ZLogWriter] =
    ZIO.serviceWith(scribeLogger => LogWriter.pureOf(scribeLogger))

  val consoleLog: ZLogWriter =
    LogWriter.pureOf[Task](LogLevels.Trace)

  def consoleLogUpToLevel[LL <: LogLevel](minLevel: LL): ZLogWriter =
    LogWriter.pureOf[Task](minLevel)

  val noOpLog: ZLogWriter =
    LogWriter.of[Id](()).liftT

  val log4sLayerFromName: RLayer[ZLogName, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { name =>
      log4sFromName.provideEnvironment(ZEnvironment(name.x))
    })

  val log4sLayerFromLogger: RLayer[ZLog4sLogger, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { l =>
      log4sFromLogger.provideEnvironment(ZEnvironment(l))
    })

  val julLayerFromLogger: RLayer[ZJulLogger, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { l =>
      julFromLogger.provideEnvironment(ZEnvironment(l))
    })

  val scribeLayerFromName: RLayer[ZLogName, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { name =>
      scribeFromName.provideEnvironment(ZEnvironment(name.x))
    })

  val scribeLayerFromLogger: RLayer[ZScribeLogger, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { l =>
      scribeFromLogger.provideEnvironment(ZEnvironment(l))
    })

  val consoleLogLayer: ULayer[ZLogWriter] =
    ZLayer.succeed(consoleLog)

  def consoleLogLayerUpToLevel[LL <: LogLevel](minLevel: LL): ULayer[ZLogWriter] =
    ZLayer.succeed(consoleLogUpToLevel(minLevel))

  val noOpLogLayer: ULayer[ZLogWriter] =
    ZLayer.succeed(noOpLog)

  private[this] object instances {
    private[zio] implicit final val taskEffectSuspension: EffectSuspension[Task] =
      new EffectSuspension[Task] {
        def suspend[A](a: =>A): Task[A] = ZIO.attempt(a)
      }

    private[zio] implicit final val uioEffectSuspension: EffectSuspension[UIO] =
      new EffectSuspension[UIO] {
        def suspend[A](a: =>A): UIO[A] = ZIO.succeed(a)
      }

    private[zio] implicit final def functorInstances[
      R,
      E
    ]: log.effect.internal.Functor[ZIO[R, E, *]] =
      new log.effect.internal.Functor[ZIO[R, E, *]] {
        def fmap[A, B](f: A => B): ZIO[R, E, A] => ZIO[R, E, B] = _ map f
      }

    implicit final class NoOpLogT(private val _underlying: LogWriter[Id]) extends AnyVal {
      def liftT: ZLogWriter =
        new LogWriter[Task] {
          override def write[A: Show](level: LogLevel, a: =>A): Task[Unit] = ZIO.unit
        }
    }
  }
}
