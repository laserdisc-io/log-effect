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

import log.effect.internal
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

import scala.annotation.nowarn

final class LogWriterResolutionTest extends AnyWordSpecLike with Matchers {
  "the construction" should {
    "correctly infer a valid log4s constructor for ZIO" in {
      import _root_.zio.{Task, ZEnvironment, ZIO}
      import log.effect.internal.{EffectSuspension, Functor}
      import log.effect.zio.ZioLogWriter.log4sFromLogger
      import log.effect.{LogWriter, LogWriterConstructor}
      import org.{log4s => l4s}

      def c: Task[l4s.Logger] => Task[LogWriter[Task]] = {
        implicit def F: EffectSuspension[Task] = ???
        implicit def FF: Functor[Task]         = ???
        implicitly[LogWriterConstructor[l4s.Logger, Task, Task]].construction
      }

      def cPure: l4s.Logger => LogWriter[Task] = {
        implicit def F: EffectSuspension[Task] = ???
        implicitly[LogWriterConstructor[l4s.Logger, internal.Id, Task]].construction
      }

      @nowarn def l1: Task[LogWriter[Task]] =
        c(ZIO.attempt(l4s.getLogger("test")))

      @nowarn def pureL1: LogWriter[Task] =
        cPure(l4s.getLogger("test"))

      @nowarn def l2: Task[LogWriter[Task]] =
        log4sFromLogger.provideEnvironment(ZEnvironment(l4s.getLogger("test")))
    }

    "correctly infer a valid jul constructor for IO" in {
      import java.util.{logging => jul}

      import _root_.zio.{Task, ZEnvironment, ZIO}
      import log.effect.internal.{EffectSuspension, Functor}
      import log.effect.zio.ZioLogWriter.julFromLogger
      import log.effect.{LogWriter, LogWriterConstructor}

      def c: Task[jul.Logger] => Task[LogWriter[Task]] = {
        implicit def F: EffectSuspension[Task] = ???
        implicit def FF: Functor[Task]         = ???
        implicitly[LogWriterConstructor[jul.Logger, Task, Task]].construction
      }

      def cPure: jul.Logger => LogWriter[Task] = {
        implicit def F: EffectSuspension[Task] = ???
        implicitly[LogWriterConstructor[jul.Logger, internal.Id, Task]].construction
      }

      @nowarn def l1: Task[LogWriter[Task]] =
        c(ZIO.attempt(jul.Logger.getGlobal))

      @nowarn def pureL1: LogWriter[Task] =
        cPure(jul.Logger.getGlobal)

      @nowarn def l2: Task[LogWriter[Task]] =
        julFromLogger.provideEnvironment(ZEnvironment(jul.Logger.getGlobal))
    }
  }

  "the LogWriterConstructor0 of IO" should {
    "correctly infer a valid console constructor for IO" in {
      import _root_.zio.Task
      import log.effect.internal.{EffectSuspension, Id}
      import log.effect.zio.ZioLogWriter.{consoleLog, consoleLogUpToLevel}
      import log.effect.{LogLevel, LogLevels, LogWriter, LogWriterConstructor}

      def c[L <: LogLevel]: L => LogWriter[Task] = {
        implicit def F: EffectSuspension[Task] = ???
        implicitly[LogWriterConstructor[L, Id, Task]].construction
      }

      @nowarn def l1: LogWriter[Task] = c(LogLevels.Trace)

      @nowarn def l2: LogWriter[Task] = consoleLog

      @nowarn def l3: LogWriter[Task] = c(LogLevels.Info)

      @nowarn def l4: LogWriter[Task] = consoleLogUpToLevel(LogLevels.Info)
    }

    "correctly infer a valid no-op constructor for an F[_] given an implicit evidence" in {
      import _root_.zio.Task
      import log.effect.zio.ZioLogWriter.noOpLog
      import log.effect.internal.Id
      import log.effect.{LogWriter, LogWriterConstructor}

      def c: Unit => LogWriter[Id] =
        implicitly[LogWriterConstructor[Unit, Id, Id]].construction

      @nowarn def l1: LogWriter[Id] = c(())

      @nowarn def l2: LogWriter[Task] = noOpLog
    }

    "not be able to infer a no-op constructor for zio Task without lifting (see ZioLogWriter.noOpLog)" in {
      """
        |import log.effect.internal.Id
        |import log.effect.{LogWriter, LogWriterConstructor}
        |import _root_.zio.Task
        |
        |def c: Unit => LogWriter[Task] =
        |  implicitly[LogWriterConstructor[Unit, Id, Task]].construction
      """.stripMargin shouldNot compile
    }
  }
}
