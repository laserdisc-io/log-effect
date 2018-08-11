package log.effect
package fs2

import java.util.{ logging => jul }

import cats.Applicative
import cats.effect.Sync
import log.effect.LogWriter.{ Console, Jul, Log4s, NoOp }
import log.effect.internal.EffectSuspension
import org.{ log4s => l4s }

object SyncLogWriter {

  def log4sLog[F[_]: Sync](fa: F[l4s.Logger]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Log4s)
    constructor(fa)
  }

  def log4sLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Log4s)
    constructor(F.delay(l4s.getLogger(c)))
  }

  def log4sLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Log4s)
    constructor(F.delay(l4s.getLogger(n)))
  }

  def julLog[F[_]: Sync](fa: F[jul.Logger]): F[LogWriter[F]] = {
    val constructor = LogWriterConstructor1[F](Jul)
    constructor(fa)
  }

  def consoleLog[F[_]: Sync]: LogWriter[F] = {
    val constructor = LogWriterConstructor0[F](Console)
    constructor()
  }

  def consoleLogUpToLevel[F[_]]: ConsoleLogPartial[F] =
    new ConsoleLogPartial[F]

  final private[effect] class ConsoleLogPartial[F[_]](private val d: Boolean = true)
      extends AnyVal {
    def apply[LL <: LogLevel](minLevel: LL)(implicit F: Sync[F]): LogWriter[F] = {
      val constructor = LogWriterConstructor0[F](Console, minLevel)
      constructor()
    }
  }

  def noOpLog[F[_]: Applicative]: LogWriter[F] = {
    val constructor = LogWriterConstructor0[F](NoOp)
    constructor()
  }

  implicit final private def syncInstance[F[_]](implicit F: Sync[F]): EffectSuspension[F] =
    new EffectSuspension[F] {
      def suspend[A](a: =>A): F[A] = F.delay(a)
    }
}
