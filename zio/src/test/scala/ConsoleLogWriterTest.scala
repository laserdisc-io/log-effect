import java.io.{ ByteArrayOutputStream, PrintStream }

import log.effect.LogLevels._
import log.effect.zio.ZioLogWriter.{ consoleLogZio, consoleLogZioUpToLevel }
import org.scalatest.{ Matchers, WordSpecLike }
import scalaz.zio
import scalaz.zio.IO

final class ConsoleLogWriterTest extends WordSpecLike with Matchers with zio.App {

  private def capturedConsoleOutOf(write: IO[Exception, Unit]): String = {
    val lowerStream = new ByteArrayOutputStream()
    val outStream   = new PrintStream(lowerStream)

    Console.withOut(outStream)(unsafeRun(write))

    lowerStream.toString
  }

  "the console LogWriter's syntax for string message" should {

    "print the expected trace to the console" in {

      val out = capturedConsoleOutOf(
        consoleLogZio.trace("test message")
      )

      info(out.trim)
      out should startWith("[trace] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected info to the console" in {

      val out = capturedConsoleOutOf(
        consoleLogZio.info("test message")
      )

      info(out.trim)
      out should startWith("[info] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected debug to the console" in {

      val out = capturedConsoleOutOf(
        consoleLogZio.debug("test message")
      )

      info(out.trim)
      out should startWith("[debug] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected error to the console" in {

      val out = capturedConsoleOutOf(
        consoleLogZio.error("test message")
      )

      info(out.trim)
      out should startWith("[error] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected warn to the console" in {

      val out = capturedConsoleOutOf(
        consoleLogZio.warn("test message")
      )

      info(out.trim)
      out should startWith("[warn] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }
  }

  "the console LogWriter's syntax for failure message" should {

    "print the expected error message and the exception to the console" in {

      val out = capturedConsoleOutOf(
        consoleLogZio
          .error("I have an error message", new Throwable("oh! there's also an exception"))
      )

      info(out.trim)
      out should startWith("[error] - [")
      out should include("ScalaTest-running-ConsoleLogWriterTest] I have an error message\n")
      out should include(
        "Failed with exception java.lang.Throwable: oh! there's also an exception\n"
      )
    }
  }

  "the console LogWriter's syntax" when {

    "created with level Error" should {

      "log error messages" in {

        val out = capturedConsoleOutOf(
          consoleLogZioUpToLevel(Error).error("error message")
        )

        info(out.trim)
        out should startWith("[error] - [")
        out should endWith("ScalaTest-running-ConsoleLogWriterTest] error message\n")
      }

      "not log warn, info, debug or trace messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Error)

          (cl.warn("warn message")
            *> cl.info("info message")
            *> cl.debug("debug message")
            *> cl.trace("trace message"))
        }

        out should be(empty)
      }
    }

    "created with level Warn" should {

      "log error and warn messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Warn)
          cl.error("error message") *> cl.warn("warn message")
        }

        info(out.trim)

        out should include("[error] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] error message\n")

        out should include("[warn] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] warn message\n")
      }

      "not log info, debug or trace messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Warn)

          (cl.info("info message")
            *> cl.debug("debug message")
            *> cl.trace("trace message"))
        }

        out should be(empty)
      }
    }

    "created with level Info" should {

      "log error, warn and info messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Info)

          (cl.error("error message")
            *> cl.warn("warn message")
            *> cl.info("info message"))
        }

        info(out.trim)

        out should include("[error] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] error message\n")

        out should include("[warn] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] warn message\n")

        out should include("[info] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] info message\n")
      }

      "not log debug or trace messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Info)
          cl.debug("debug message") *> cl.trace("trace message")
        }

        out should be(empty)
      }
    }

    "created with level Debug" should {

      "log error, warn, info and debug messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Debug)

          (cl.error("error message")
            *> cl.warn("warn message")
            *> cl.info("info message")
            *> cl.debug("debug message"))
        }

        info(out.trim)

        out should include("[error] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] error message\n")

        out should include("[warn] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] warn message\n")

        out should include("[info] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] info message\n")

        out should include("[debug] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] debug message\n")
      }

      "not log trace messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Debug)
          cl.trace("trace message")
        }

        out should be(empty)
      }
    }

    "created with level Trace" should {

      "log all the messages" in {
        val out = capturedConsoleOutOf {
          val cl = consoleLogZioUpToLevel(Trace)

          (cl.error("error message")
            *> cl.warn("warn message")
            *> cl.info("info message")
            *> cl.debug("debug message")
            *> cl.trace("trace message"))
        }

        info(out.trim)

        out should include("[error] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] error message\n")

        out should include("[warn] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] warn message\n")

        out should include("[info] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] info message\n")

        out should include("[debug] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] debug message\n")

        out should include("[trace] - [")
        out should include("ScalaTest-running-ConsoleLogWriterTest] trace message\n")
      }
    }
  }

  def run(args: List[String]): IO[Nothing, ExitStatus] = IO.succeed(ExitStatus.ExitNow(0))
}
