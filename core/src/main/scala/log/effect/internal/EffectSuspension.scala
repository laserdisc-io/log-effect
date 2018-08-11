package log.effect
package internal

trait EffectSuspension[F[_]] {
  def suspend[A](a: =>A): F[A]
  final val unit: F[Unit] = suspend(())
}
