package log.effect
package fs2

import java.util.{logging => jul}

import cats.Applicative
import cats.effect.Sync
import log.effect.internal.{EffectSuspension, Id, Show}
import org.{log4s => l4s}

object SyncLogWriter {
  import instances._

  def log4sLog[F[_]: Sync](l: l4s.Logger): LogWriter[F] =
    LogWriter.pureOf[F](l)

  def log4sLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(l4s.getLogger(c)))

  def log4sLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(l4s.getLogger(n)))

  def julLog[F[_]: Sync](l: jul.Logger): LogWriter[F] =
    LogWriter.pureOf[F](l)

  def julLog[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(jul.Logger.getGlobal))

  def scribeLog[F[_]: Sync](l: scribe.Logger): LogWriter[F] =
    LogWriter.pureOf[F](l)

  def scribeLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(scribe.Logger(n)))

  def scribeLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] = {
    import scribe._
    LogWriter.of(F.delay(c.logger))
  }

  def consoleLog[F[_]: Sync]: LogWriter[F] =
    LogWriter.pureOf[F](LogLevels.Trace)

  def consoleLogUpToLevel[F[_]: Sync, LL <: LogLevel](minLevel: LL): LogWriter[F] =
    LogWriter.pureOf[F](minLevel)

  def noOpLog[F[_]: Applicative]: LogWriter[F] =
    LogWriter.of[Id](()).liftF

  private[this] object instances {
    private[fs2] implicit final def syncInstances[F[_]](implicit F: Sync[F]): EffectSuspension[F] =
      new EffectSuspension[F] {
        def suspend[A](a: =>A): F[A] = F delay a
      }

    private[fs2] implicit final def functorInstances[F[_]](
      implicit F: cats.Functor[F]
    ): internal.Functor[F] =
      new internal.Functor[F] {
        def fmap[A, B](f: A => B): F[A] => F[B] = F lift f
      }

    implicit final class NoOpLogF(private val `_`: LogWriter[Id]) extends AnyVal {
      def liftF[F[_]: Applicative]: LogWriter[F] =
        new LogWriter[F] {
          def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit] = Applicative[F].unit
        }
    }
  }
}
