package log
package effect
package internal
package syntax

import scala.language.implicitConversions

private[syntax] trait ShowSyntax {
  implicit final def showSyntax[A](a: A): ShowOps[A] = new ShowOps(a)
}

private[syntax] final class ShowOps[A](private val a: A) extends AnyVal {
  def show(implicit ev: Show[A]): String = ev show a
}
