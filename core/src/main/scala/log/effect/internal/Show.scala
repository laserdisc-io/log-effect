package log.effect
package internal

trait Show[A] {
  def show(a: A): String
}

object Show {

  implicit val stringShow: Show[String] =
    new Show[String] {
      def show(x: String): String = x
    }
}
