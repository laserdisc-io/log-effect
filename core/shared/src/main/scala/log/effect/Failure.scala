/*
 * Copyright (c) 2018-2025 LaserDisc
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

import log.effect.internal.Show

final class Failure(val msg: String, val th: Throwable)

object Failure {
  def apply(msg: String, th: Throwable): Failure =
    new Failure(msg, th)

  def unapply(arg: Failure): Option[(String, Throwable)] =
    Some((arg.msg, arg.th))

  implicit def failureShow(implicit ev: Show[Throwable]): internal.Show[Failure] =
    new Show[Failure] {
      def show(t: Failure): String =
        s"""${t.msg}
           |  ${ev.show(t.th)}
           |  Failed with exception ${t.th}
           |  Stack trace:
           |    ${t.th.getStackTrace.toList.mkString("\n|    ")}""".stripMargin
    }
}
