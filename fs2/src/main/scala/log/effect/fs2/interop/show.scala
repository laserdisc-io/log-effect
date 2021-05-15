package log.effect
package fs2
package interop

private[fs2] trait show {
  implicit def internalShowInstances[A](implicit ev: cats.Show[A]): internal.Show[A] =
    (a: A) => ev show a

  implicit def catsShowInstances[A](implicit ev: internal.Show[A]): cats.Show[A] =
    (a: A) => ev show a
}

object show extends show
