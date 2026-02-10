/*
 * Copyright (c) 2018-2026 LaserDisc
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

import _root_.zio.{Runtime, Task, Unsafe, ZEnvironment, ZIO}
import org.log4s.{LoggedEvent, Logger, TestAppender, getLogger}
import ch.qos.logback.classic.spi.ILoggingEvent

final class MyTestAppender extends TestAppender {
  override def start(): Unit = {
    println(">>>>>>>>>>>>>>> starting test appended")
    super.start()
  }

  override def stop(): Unit = {
    println(">>>>>>>>>>>>>>> stopping test appender")
    super.stop()
  }

  override def append(event: ILoggingEvent): Unit = {
    println(">>>>>>>>>>>>>>> appending to test appender")
    super.append(event)
  }
}

trait TestLogCapture {

  protected final def capturedLog4sOutOf(
    logWrite: ZIO[Logger, Throwable, Unit]
  ): Option[LoggedEvent] = {
    val logger = getLogger("Test Logger")
    TestAppender.withAppender() {
      val loggingAction: Task[Unit] =
        logWrite.provideEnvironment(ZEnvironment(logger))
      Unsafe.unsafe { implicit unsafe =>
        Runtime.default.unsafe.run(loggingAction).getOrThrowFiberFailure()
      }
      TestAppender.dequeue
    }
  }
}
