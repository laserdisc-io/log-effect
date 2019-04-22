package log
package effect
package fs2

import java.util.{ logging => jul }

import cats.effect.Sync
import log.effect.internal.{ EffectSuspension, Id }
import org.{ log4s => l4s }

object SyncLogWriter {

  import instances._

  def log4sLog[F[_]: Sync](fa: F[l4s.Logger]): F[LogWriter[F]] =
    LogWriter.of[F](fa)

  def log4sLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of[F](F.delay(l4s.getLogger(c)))

  def log4sLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of[F](F.delay(l4s.getLogger(n)))

  def julLog[F[_]: Sync](fa: F[jul.Logger]): F[LogWriter[F]] =
    LogWriter.of[F](fa)

  def julLog[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of[F](F.delay(jul.Logger.getGlobal))

  def scribeLog[F[_]: Sync](fa: F[scribe.Logger]): F[LogWriter[F]] =
    LogWriter.of[F](fa)

  def scribeLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of[F](F.delay(scribe.Logger(n)))

  def scribeLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] = {
    import scribe._
    LogWriter.of[F](F.delay(c.logger))
  }

  def consoleLog[F[_]: Sync]: LogWriter[F] =
    LogWriter.from[Id].runningEffect[F](LogLevels.Trace)

  def consoleLogUpToLevel[F[_]: Sync, LL <: LogLevel](minLevel: LL): LogWriter[F] =
    LogWriter.from[Id].runningEffect[F](minLevel)

  def noOpLog[F[_]]: LogWriter[Id] =
    LogWriter.of[Id](())

  private[this] object instances {

    implicit final private[fs2] def syncInstances[F[_]](implicit F: Sync[F]): EffectSuspension[F] =
      new EffectSuspension[F] {
        def suspend[A](a: =>A): F[A] = F delay a
      }

    implicit final private[fs2] def functorInstances[F[_]](
      implicit F: cats.Functor[F]
    ): internal.Functor[F] =
      new internal.Functor[F] {
        def fmap[A, B](f: A => B): F[A] => F[B] = F lift f
      }
  }
}
