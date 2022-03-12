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

import com.github.ghik.silencer.silent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

@silent final class ReadmeConstructionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "Cats effect Sync construction snippets should compile" in {
    import java.util.{logging => jul}

    import cats.effect.Sync
    import cats.syntax.functor._
    import log.effect.fs2.SyncLogWriter._
    import log.effect.{LogLevels, LogWriter}
    import org.{log4s => l4s}

    sealed abstract class App[F[_]](implicit F: Sync[F]) {
      val log4s1: F[LogWriter[F]] =
        F.delay(l4s.getLogger("test")) map log4sLog[F]

      val log4s2: F[LogWriter[F]] = log4sLog("a logger")

      val log4s3: F[LogWriter[F]] = {
        case class LoggerClass()
        log4sLog(classOf[LoggerClass])
      }

      val jul1: F[LogWriter[F]] =
        F.delay(jul.Logger.getLogger("a logger")) map julLog[F]

      val jul2: F[LogWriter[F]] = julLog

      val scribe1: F[LogWriter[F]] =
        F.delay(scribe.Logger("a logger")) map scribeLog[F]

      val scribe2: F[LogWriter[F]] = scribeLog("a logger")

      val scribe3: F[LogWriter[F]] = {
        case class LoggerClass()
        scribeLog(classOf[LoggerClass])
      }

      val console1: LogWriter[F] = consoleLog

      val console2: LogWriter[F] = consoleLogUpToLevel(LogLevels.Warn)

      val noOp: LogWriter[F] = noOpLog[F]
    }
  }

  "Fs2 streams construction snippets should compile" in {
    import java.util.{logging => jul}

    import cats.effect.Sync
    import cats.syntax.flatMap._
    import fs2.Stream
    import log.effect.fs2.Fs2LogWriter._
    import log.effect.{LogLevels, LogWriter}
    import org.{log4s => l4s}

    sealed abstract class App[F[_]](implicit F: Sync[F]) {
      val log4s1: fs2.Stream[F, LogWriter[F]] =
        Stream.eval(F.delay(l4s.getLogger("test"))) >>= log4sLogStream[F]

      val log4s2: fs2.Stream[F, LogWriter[F]] = log4sLogStream("a logger")

      val log4s3: fs2.Stream[F, LogWriter[F]] = {
        case class LoggerClass()
        log4sLogStream(classOf[LoggerClass])
      }

      val jul1: fs2.Stream[F, LogWriter[F]] =
        Stream.eval(F.delay(jul.Logger.getLogger("a logger"))) >>= julLogStream[F]

      val jul2: fs2.Stream[F, LogWriter[F]] = julLogStream

      val scribe1: fs2.Stream[F, LogWriter[F]] =
        Stream.eval(F.delay(scribe.Logger("a logger"))) >>= scribeLogStream[F]

      val scribe2: fs2.Stream[F, LogWriter[F]] = scribeLogStream("a logger")

      val scribe3: fs2.Stream[F, LogWriter[F]] = {
        case class LoggerClass()
        scribeLogStream(classOf[LoggerClass])
      }

      val console1: fs2.Stream[F, LogWriter[F]] = consoleLogStream

      val console2: fs2.Stream[F, LogWriter[F]] = consoleLogStreamUpToLevel(LogLevels.Warn)

      val noOp: fs2.Stream[F, LogWriter[F]] = noOpLogStream
    }
  }
}
