package log
package effect

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
