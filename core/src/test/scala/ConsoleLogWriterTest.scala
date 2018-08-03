import java.io.{ ByteArrayOutputStream, PrintStream }

import cats.effect.IO
import log.effect.LogWriter.consoleLog
import org.scalatest.{ Matchers, WordSpecLike }

final class ConsoleLogWriterTest extends WordSpecLike with Matchers {

  private def capturedConsoleOutOf[A](thunk: =>A): IO[String] =
    IO {
      val lowerStream = new ByteArrayOutputStream()
      val outStream   = new PrintStream(lowerStream)

      Console.withOut(outStream)(thunk)

      lowerStream.toString
    }

  "the console LogWriter's syntax for string message" should {

    "print the expected trace to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog[IO]
          .trace("test message")
          .unsafeRunSync()
      ).unsafeRunSync()

      info(out.trim)
      out should startWith("[trace] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected info to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog[IO]
          .info("test message")
          .unsafeRunSync()
      ).unsafeRunSync()

      info(out.trim)
      out should startWith("[info] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected debug to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog[IO]
          .debug("test message")
          .unsafeRunSync()
      ).unsafeRunSync()

      info(out.trim)
      out should startWith("[debug] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected error to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog[IO]
          .error("test message")
          .unsafeRunSync()
      ).unsafeRunSync()

      info(out.trim)
      out should startWith("[error] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }

    "print the expected warn to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog[IO]
          .warn("test message")
          .unsafeRunSync()
      ).unsafeRunSync()

      info(out.trim)
      out should startWith("[warn] - [")
      out should endWith("ScalaTest-running-ConsoleLogWriterTest] test message\n")
    }
  }

  "the console LogWriter's syntax for failure message" should {

    "print the expected error message and the exception to the console" in {

      val out = capturedConsoleOutOf(
        consoleLog[IO]
          .error("I have an error message", new Throwable("oh! there's also an exception"))
          .unsafeRunSync()
      ).unsafeRunSync()

      info(out.trim)
      out should startWith("[error] - [")
      out should include("ScalaTest-running-ConsoleLogWriterTest] I have an error message\n")
      out should include(
        "Failed with exception java.lang.Throwable: oh! there's also an exception\n"
      )
    }
  }
}
