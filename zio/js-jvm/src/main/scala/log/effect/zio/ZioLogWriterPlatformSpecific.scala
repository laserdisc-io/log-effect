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
import log.effect.LogWriter
import org.{log4s => l4s}

trait ZioLogWriterPlatformSpecific {
  import instances._

  final type ZLog4sLogger = l4s.Logger

  val log4sFromLogger: URIO[ZLog4sLogger, ZLogWriter] =
    ZIO.serviceWith(log4sLogger => LogWriter.pureOf[Task](log4sLogger))

  val log4sFromName: RIO[String, ZLogWriter] =
    ZIO.serviceWithZIO(name => LogWriter.of[Task](ZIO.attempt(l4s.getLogger(name))))

  val log4sFromClass: RIO[Class[_], ZLogWriter] =
    ZIO.serviceWithZIO((clazz: Class[_]) => LogWriter.of[Task](ZIO.attempt(l4s.getLogger(clazz))))

  val log4sLayerFromName: RLayer[ZLogName, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { name =>
      log4sFromName.provideEnvironment(ZEnvironment(name.x))
    })

  val log4sLayerFromLogger: RLayer[ZLog4sLogger, ZLogWriter] =
    ZLayer(ZIO.serviceWithZIO { l =>
      log4sFromLogger.provideEnvironment(ZEnvironment(l))
    })

}
