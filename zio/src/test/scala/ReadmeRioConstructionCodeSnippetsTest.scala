/*
 * Copyright (c) 2022 LaserDisc
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

@nowarn final class ReadmeRioConstructionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "Zio RIO construction snippets should compile" in {
    import java.util.{logging => jul}

    import log.effect.zio.ZioLogWriter._
    import log.effect.{LogLevels, LogWriter}
    import org.{log4s => l4s}
    import zio._

    object Setup {
      final case class AConfig(logName: String)
      final val aLogName                                            = "a logger"
      final def someZioProgramUsingLogs: RIO[LogWriter[Task], Unit] = ???
    }

    sealed abstract class App {
      import Setup._

      // Case 1: from a possible config in a Layer (gives a Layer)
      val log4sCase1: RLayer[Has[AConfig], ZLogWriter] =
        ZLayer.fromServiceM { config =>
          log4sFromName.provide(config.logName)
        }
      val scribe4sCase1: RLayer[Has[AConfig], ZLogWriter] =
        ZLayer.fromServiceM { config =>
          scribeFromName.provide(config.logName)
        }

      // Case 2: from a name
      val log4sCase2: Task[Unit] =
        (log4sFromName >>> someZioProgramUsingLogs) provide aLogName

      val scribeCase2: Task[Unit] =
        (scribeFromName >>> someZioProgramUsingLogs) provide aLogName

      // Case 3: from a logger
      val log4sCase3: Task[Unit] =
        Task.effect(l4s.getLogger(aLogName)) >>= { logger =>
          (log4sFromLogger >>> someZioProgramUsingLogs) provide logger
        }
      val julCase3: Task[Unit] =
        Task.effect(jul.Logger.getLogger(aLogName)) >>= { logger =>
          (julFromLogger >>> someZioProgramUsingLogs) provide logger
        }
      val scribeCase3: Task[Unit] =
        Task.effect(scribe.Logger(aLogName)) >>= { logger =>
          (scribeFromLogger >>> someZioProgramUsingLogs) provide logger
        }

      // Case 4: from a class
      val log4sCase4: Task[Unit] = {
        case class LoggerClass();
        (log4sFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
      }
      val scribeCase4: Task[Unit] = {
        case class LoggerClass();
        (scribeFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
      }

      // Case 5 (Jul): from global logger object
      val julCase5: Task[Unit] =
        julGlobal >>> someZioProgramUsingLogs

      // Case 6: console logger
      val console1: Task[Unit] =
        someZioProgramUsingLogs provide consoleLog

      val console2: Task[Unit] =
        someZioProgramUsingLogs provide consoleLogUpToLevel(LogLevels.Warn)

      // Case 7: No-op logger
      val noOp: Task[Unit] =
        someZioProgramUsingLogs provide noOpLog
    }
  }
}
