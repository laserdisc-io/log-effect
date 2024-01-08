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

import log.effect.zio.TestLogCapture
import log.effect.zio.ZioLogWriter.log4sFromLogger
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class Log4sLogWriterTest extends AnyWordSpecLike with Matchers with TestLogCapture {

  "the log4s LogWriter's syntax for messages" should {
    "print the expected trace log" in {
      val logged = capturedLog4sOutOf(
        log4sFromLogger.flatMap(_.trace("test trace message"))
      )

      logged.get.level.levelStr should be("TRACE")
      logged.get.throwable should be(empty)
      logged.get.message should be("test trace message")
    }

    "print the expected debug log" in {
      val logged = capturedLog4sOutOf(
        log4sFromLogger.flatMap(_.debug("test debug message"))
      )

      logged.get.level.levelStr should be("DEBUG")
      logged.get.throwable should be(empty)
      logged.get.message should be("test debug message")
    }

    "print the expected info log" in {
      val logged = capturedLog4sOutOf(
        log4sFromLogger.flatMap(_.info("test info message"))
      )

      logged.get.level.levelStr should be("INFO")
      logged.get.throwable should be(empty)
      logged.get.message should be("test info message")
    }

    "print the expected warn log" in {
      val logged = capturedLog4sOutOf(
        log4sFromLogger.flatMap(_.warn("test warn message"))
      )

      logged.get.level.levelStr should be("WARN")
      logged.get.throwable should be(empty)
      logged.get.message should be("test warn message")
    }

    "print the expected error log" in {
      val logged = capturedLog4sOutOf(
        log4sFromLogger.flatMap(_.error("test error message"))
      )

      logged.get.level.levelStr should be("ERROR")
      logged.get.throwable should be(empty)
      logged.get.message should be("test error message")
    }

    "print the expected exception log" in {
      val logged = capturedLog4sOutOf(
        log4sFromLogger.flatMap(_.error(new Exception("Test Exception")))
      )

      logged.get.level.levelStr should be("ERROR")
      logged.get.throwable should be(empty)
      logged.get.message should startWith(
        "Failed with exception java.lang.Exception: Test Exception\n  Stack trace:\n"
      )
    }
  }
}
