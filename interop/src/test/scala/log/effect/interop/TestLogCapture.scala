package log.effect
package interop

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.flatMap._
import org.log4s.{LoggedEvent, TestAppender, getLogger}
import org.slf4j.Logger

trait TestLogCapture {

  protected final def capturedLog4sOutOf(
    logWrite: Logger => IO[Unit]
  ): Seq[LoggedEvent] = {
    val loggingAction = IO.delay(getLogger("Test Logger").logger) >>= { logger =>
      TestAppender.withAppender() {
        logWrite(logger)
      }
    }
    loggingAction.unsafeRunSync()

    TestAppender.dequeueAll()
  }
}
