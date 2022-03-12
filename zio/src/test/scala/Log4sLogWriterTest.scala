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
import log.effect.zio.TestLogCapture
import log.effect.zio.ZioLogWriter.log4sFromLogger
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class Log4sLogWriterTest extends AnyWordSpecLike with Matchers with TestLogCapture {

  "the log4s LogWriter's syntax for messages" should {
    "print the expected trace log" in {
      @silent
      val Some(logged) = capturedLog4sOutOf(
        log4sFromLogger >>= (_.trace("test trace message"))
      )

      logged.level.levelStr should be("TRACE")
      logged.throwable should be(empty)
      logged.message should be("test trace message")
    }

    "print the expected debug log" in {
      @silent
      val Some(logged) = capturedLog4sOutOf(
        log4sFromLogger >>= (_.debug("test debug message"))
      )

      logged.level.levelStr should be("DEBUG")
      logged.throwable should be(empty)
      logged.message should be("test debug message")
    }

    "print the expected info log" in {
      @silent
      val Some(logged) = capturedLog4sOutOf(
        log4sFromLogger >>= (_.info("test info message"))
      )

      logged.level.levelStr should be("INFO")
      logged.throwable should be(empty)
      logged.message should be("test info message")
    }

    "print the expected warn log" in {
      @silent
      val Some(logged) = capturedLog4sOutOf(
        log4sFromLogger >>= (_.warn("test warn message"))
      )

      logged.level.levelStr should be("WARN")
      logged.throwable should be(empty)
      logged.message should be("test warn message")
    }

    "print the expected error log" in {
      @silent
      val Some(logged) = capturedLog4sOutOf(
        log4sFromLogger >>= (_.error("test error message"))
      )

      logged.level.levelStr should be("ERROR")
      logged.throwable should be(empty)
      logged.message should be("test error message")
    }

    "print the expected exception log" in {
      @silent
      val Some(logged) = capturedLog4sOutOf(
        log4sFromLogger >>= (_.error(new Exception("Test Exception")))
      )

      logged.level.levelStr should be("ERROR")
      logged.throwable should be(empty)
      logged.message should startWith(
        "Failed with exception java.lang.Exception: Test Exception\n  Stack trace:\n"
      )
    }
  }
}
