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
      val log4sCase1: RLayer[AConfig, ZLogWriter] =
        ZLayer(ZIO.serviceWithZIO { c =>
          log4sFromName.provideEnvironment(ZEnvironment(c.logName))
        })
      val scribe4sCase1: RLayer[AConfig, ZLogWriter] =
        ZLayer(ZIO.serviceWithZIO { c =>
          scribeFromName.provideEnvironment(ZEnvironment(c.logName))
        })

      // Case 2: from a name
      val log4sCase2: Task[Unit] =
        log4sFromName.provideEnvironment(ZEnvironment(aLogName)).flatMap { logger =>
          someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
        }
      val scribeCase2: Task[Unit] =
        scribeFromName.provideEnvironment(ZEnvironment(aLogName)).flatMap { logger =>
          someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
        }

      // Case 3: from a logger
      val log4sCase3: Task[Unit] =
        for {
          logger    <- ZIO.attempt(l4s.getLogger(aLogName))
          logWriter <- log4sFromLogger.provideEnvironment(ZEnvironment(logger))
          _         <- someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logWriter))
        } yield ()
      val julCase3: Task[Unit] =
        for {
          logger    <- ZIO.attempt(jul.Logger.getLogger(aLogName))
          logWriter <- julFromLogger.provideEnvironment(ZEnvironment(logger))
          _         <- someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logWriter))
        } yield ()
      val scribeCase3: Task[Unit] =
        for {
          logger    <- ZIO.attempt(scribe.Logger(aLogName))
          logWriter <- scribeFromLogger.provideEnvironment(ZEnvironment(logger))
          _         <- someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logWriter))
        } yield ()

      // Case 4: from a class
      val log4sCase4: Task[Unit] = {
        case class LoggerClass();
        log4sFromClass.provideEnvironment(ZEnvironment(classOf[LoggerClass])).flatMap { logger =>
          someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
        }
      }
      val scribeCase4: Task[Unit] = {
        case class LoggerClass();
        scribeFromClass.provideEnvironment(ZEnvironment(classOf[LoggerClass])).flatMap { logger =>
          someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
        }
      }

      // Case 5 (Jul): from global logger object
      val julCase5: Task[Unit] =
        julGlobal.flatMap(logger =>
          someZioProgramUsingLogs.provideEnvironment(ZEnvironment(logger))
        )

      // Case 6: console logger
      val console1: Task[Unit] =
        someZioProgramUsingLogs.provideEnvironment(ZEnvironment(consoleLog))

      val console2: Task[Unit] =
        someZioProgramUsingLogs.provideEnvironment(
          ZEnvironment(consoleLogUpToLevel(LogLevels.Warn))
        )

      // Case 7: No-op logger
      val noOp: Task[Unit] =
        someZioProgramUsingLogs.provideEnvironment(ZEnvironment(noOpLog))
    }
  }
}
