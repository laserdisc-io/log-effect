package log.effect
package internal
package syntax

import scala.language.implicitConversions

private[syntax] trait FunctorSyntax {
  implicit final def functorSyntax[F[_], A](fa: F[A]): FunctorOps[F, A] = new FunctorOps(fa)
}

final private[syntax] class FunctorOps[F[_], A](private val fa: F[A]) extends AnyVal {
  def map[B](f: A => B)(implicit F: Functor[F]): F[B] = F.fmap(f)(fa)
}
