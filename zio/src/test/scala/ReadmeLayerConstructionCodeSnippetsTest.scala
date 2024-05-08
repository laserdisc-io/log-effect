/*
 * Copyright (c) 2018-2024 LaserDisc
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.annotation.nowarn

@nowarn final class ReadmeLayerConstructionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "Zio Layer construction snippets should compile" in {
    import java.util.{logging => jul}

    import log.effect.zio._, log.effect.zio.ZioLogWriter._
    import org.{log4s => l4s}
    import zio._

    object Setup {
      private val aLogName      = "a logger"
      private val aLog4sLogger  = l4s.getLogger("a log4s logger")
      private val aJulLogger    = jul.Logger.getLogger("a jul logger")
      private val aScribeLogger = scribe.Logger("a Scribe logger")

      final case class LoggerClass()
      final case class AConfig(logName: String)

      final val aConfigLive: ULayer[AConfig] =
        ZLayer.succeed(AConfig(aLogName))

      final val logNameLive: ULayer[ZLogName] =
        ZLayer.succeed(LogName(aLogName))

      final val log4sLoggerLive: ULayer[ZLog4sLogger] =
        ZLayer.succeed(aLog4sLogger)

      final val julLoggerLive: ULayer[ZJulLogger] =
        ZLayer.succeed(aJulLogger)

      final val scribeLoggerLive: ULayer[ZScribeLogger] =
        ZLayer.succeed(aScribeLogger)
    }

    sealed abstract class App {
      import Setup._

      // Case 1: from a possible config
      val logNameLiveFromConfig: ULayer[ZLogName] =
        aConfigLive >>> ZLayer(ZIO.service[AConfig].map(c => LogName(c.logName)))

      val log4sCase1: TaskLayer[ZLogWriter] =
        logNameLiveFromConfig >>> log4sLayerFromName

      val scribeCase1: TaskLayer[ZLogWriter] =
        logNameLiveFromConfig >>> scribeLayerFromName

      // Case 2: from a name
      val log4sCase2: TaskLayer[ZLogWriter] =
        logNameLive >>> log4sLayerFromName

      val scribeCase2: TaskLayer[ZLogWriter] =
        logNameLive >>> scribeLayerFromName

      // Case 3: from a logger
      val log4sCase3: TaskLayer[ZLogWriter] =
        log4sLoggerLive >>> log4sLayerFromLogger

      val julCase3: TaskLayer[ZLogWriter] =
        julLoggerLive >>> julLayerFromLogger

      val scribeCase3: TaskLayer[ZLogWriter] =
        scribeLoggerLive >>> scribeLayerFromLogger
    }
  }
}
