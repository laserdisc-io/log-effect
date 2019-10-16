import java.io.{ ByteArrayOutputStream, PrintStream }

import _root_.zio.{ Exit, IO, Task, ZEnv, ZIO }
import log.effect.LogLevels._
import log.effect.zio.ZioLogWriter.{ consoleLog, consoleLogUpToLevel }
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class ConsoleLogWriterTest extends AnyWordSpecLike with Matchers with zio.App {

  private[this] def capturedConsoleOutOf(aWrite: Task[Unit]): String = {
    val lowerStream = new ByteArrayOutputStream()
    val outStream   = new PrintStream(lowerStream)

    Console.withOut(outStream)(unsafeRun(aWrite))

    lowerStream.toString
  }

  "the console LogWriter's syntax for string message" should {

    "print the expected trace to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog.trace("test message")
      )

      info(out.trim)
      out should startWith("[trace] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected info to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog.info("test message")
      )

      info(out.trim)
      out should startWith("[info] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected debug to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog.debug("test message")
      )

      info(out.trim)
      out should startWith("[debug] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected error to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog.error("test message")
      )

      info(out.trim)
      out should startWith("[error] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected warn to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog.warn("test message")
      )

      info(out.trim)
      out should startWith("[warn] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }
  }

  "the console LogWriter's syntax for failure message" should {

    "print the expected error message and the exception to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog
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
          consoleLogUpToLevel(Error).error("error message")
        )

        info(out.trim)
        out should startWith("[error] - [")
        out should endWith("ScalaTest-running-ConsoleLogWriterTest] error message\n")
      }

      "not log warn, info, debug or trace messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogUpToLevel(Error)

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
          val cl = consoleLogUpToLevel(Warn)
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
          val cl = consoleLogUpToLevel(Warn)

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
          val cl = consoleLogUpToLevel(Info)

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
          val cl = consoleLogUpToLevel(Info)
          cl.debug("debug message") *> cl.trace("trace message")
        }

        out should be(empty)
      }
    }

    "created with level Debug" should {

      "log error, warn, info and debug messages" in {

        val out = capturedConsoleOutOf {
          val cl = consoleLogUpToLevel(Debug)

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
          val cl = consoleLogUpToLevel(Debug)
          cl.trace("trace message")
        }

        out should be(empty)
      }
    }

    "created with level Trace" should {

      "log all the messages" in {
        val out = capturedConsoleOutOf {
          val cl = consoleLogUpToLevel(Trace)

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

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = IO.done(Exit.succeed(0))
}
