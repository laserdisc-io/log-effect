package log.effect.internal

trait Functor[F[_]] {
  def fmap[A, B](f: A => B): F[A] => F[B]
}
