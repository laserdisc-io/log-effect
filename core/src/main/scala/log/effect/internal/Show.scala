package log
package effect
package internal

trait Show[A] {
  def show(a: A): String
}

object Show {
  implicit val stringShow: Show[String] =
    new Show[String] {
      def show(x: String): String = x
    }

  implicit val throwableShow: Show[Throwable] =
    new Show[Throwable] {
      def show(x: Throwable): String =
        s"""Failed with exception $x
         |  Stack trace:
         |    ${x.getStackTrace.toList.mkString("\n|    ")}""".stripMargin
    }
}
