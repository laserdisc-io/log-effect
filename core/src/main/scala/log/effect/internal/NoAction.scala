package log
package effect
package internal

trait NoAction[F[_]] {
  def unit: F[Unit]
}
