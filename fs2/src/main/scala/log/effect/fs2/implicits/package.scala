package log.effect
package fs2

package object implicits {

  implicit def showInstances[A](implicit ev: cats.Show[A]): internal.Show[A] =
    new internal.Show[A] {
      def show(a: A): String = ev show a
    }
}
