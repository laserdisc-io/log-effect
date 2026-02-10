/*
 * Copyright (c) 2018-2025 LaserDisc
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
import log.effect.internal.Id

import java.util.{logging => jul}

object ZioLogWriter extends ZioLogWriterPlatformSpecific {
  import instances._

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
}
