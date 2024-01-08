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

import log.effect.internal.Show
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

import scala.annotation.nowarn

final class LogWriterSyntaxResolutionTest extends AnyWordSpecLike with Matchers {
  case class A()
  object A {
    implicit val aShow: Show[A] = new Show[A] {
      override def show(t: A): String = ???
    }
  }

  "the LogWriter's syntax" should {
    "be inferred without extra import" in {
      import log.effect.LogWriter

      @nowarn def test[F[_]](l: LogWriter[F]) = {
        l.trace(A())
        l.trace("test")
        l.trace(new Throwable("test"))
        l.trace("test", new Throwable("test"))

        l.debug(A())
        l.debug("test")
        l.debug(new Throwable("test"))
        l.debug("test", new Throwable("test"))

        l.info(A())
        l.info("test")
        l.info(new Throwable("test"))
        l.info("test", new Throwable("test"))

        l.error(A())
        l.error("test")
        l.error(new Throwable("test"))
        l.error("test", new Throwable("test"))

        l.warn(A())
        l.warn("test")
        l.warn(new Throwable("test"))
        l.warn("test", new Throwable("test"))
      }
    }
  }

  "the LogWriter's alias for the singleton companion" should {
    "be inferred allowing a boilerplate free mtl-style syntax" in {
      import log.effect.LogLevels.Trace
      import log.effect.LogWriter

      @nowarn def f1[F[_]: LogWriter] =
        LogWriter.write(Trace, "test")

      @nowarn def f2[F[_]: LogWriter] =
        LogWriter.trace(A())

      @nowarn def f3[F[_]: LogWriter] =
        LogWriter.debug(A())

      @nowarn def f4[F[_]: LogWriter] =
        LogWriter.info(A())

      @nowarn def f5[F[_]: LogWriter] =
        LogWriter.error(A())

      @nowarn def f6[F[_]: LogWriter] =
        LogWriter.warn(A())
    }

    "be inferred allowing a boilerplate free mtl-style syntax for errors" in {
      import log.effect.LogLevels.Error
      import log.effect.{Failure, LogWriter}

      @nowarn def f1[F[_]: LogWriter] =
        LogWriter.write(Error, Failure("test", new Exception("test exception")))

      @nowarn def f2[F[_]: LogWriter] =
        LogWriter.error(Failure("test", new Exception("test exception")))
    }
  }
}
