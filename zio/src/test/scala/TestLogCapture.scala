import com.github.ghik.silencer.silent
import org.log4s.{LoggedEvent, Logger, TestAppender, getLogger}
import zio.ZIO

trait TestLogCapture {

  protected final def capturedLog4sOutOf(
    logWrite: ZIO[Logger, Throwable, Unit]
  ): Option[LoggedEvent] = {
    @silent val loggingAction =
      ZIO.effect(getLogger("Test Logger")) >>= { logger =>
        TestAppender.withAppender() {
          logWrite provide logger
        }
      }

    zio.Runtime.default.unsafeRun(loggingAction)

    TestAppender.dequeue
  }
}
