package log.effect.internal

trait NoAction[F[_]] {
  def unit: F[Unit]
}
