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

package log.effect
package zio

import _root_.zio.{Runtime, Task, Unsafe, ZEnvironment, ZIO}
import org.log4s.{LoggedEvent, Logger, TestAppender, getLogger}

trait TestLogCapture {

  protected final def capturedLog4sOutOf(
    logWrite: ZIO[Logger, Throwable, Unit]
  ): Option[LoggedEvent] = {
    val loggingAction: Task[Unit] =
      ZIO.attempt(getLogger("Test Logger")).flatMap { logger =>
        TestAppender.withAppender() {
          logWrite.provideEnvironment(ZEnvironment(logger))
        }
      }

    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run(loggingAction).getOrThrowFiberFailure()
    }

    TestAppender.dequeue
  }
}
