package log.effect
package interop

import cats.effect.{Effect, Sync}
import cats.syntax.flatMap._
import org.log4s.{LoggedEvent, TestAppender, getLogger}
import org.slf4j.Logger

trait TestLogCapture {

  protected final def capturedLog4sOutOf[F[_]: Effect](
    logWrite: Logger => F[Unit]
  ): Seq[LoggedEvent] = {
    val loggingAction: F[Unit] =
      Sync[F].delay(getLogger("Test Logger").logger) >>= { logger =>
        TestAppender.withAppender() {
          logWrite(logger)
        }
      }

    Effect[F].toIO(loggingAction).unsafeRunSync()

    TestAppender.dequeueAll()
  }
}
