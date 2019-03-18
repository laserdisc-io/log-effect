package log
package effect
package fs2
package interop

object show {

  implicit def showInstances[A](implicit ev: cats.Show[A]): internal.Show[A] =
    new internal.Show[A] {
      def show(a: A): String = ev show a
    }
}
