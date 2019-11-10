package log
package effect
package internal

trait Functor[F[_]] {
  def fmap[A, B](f: A => B): F[A] => F[B]
}

object Functor {
  implicit val idFunctor: Functor[Id] =
    new Functor[Id] {
      def fmap[A, B](f: A => B): Id[A] => Id[B] = f
    }
}
