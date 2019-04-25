package log
package effect
package fs2

import java.util.{ logging => jul }

import cats.Applicative
import cats.effect.Sync
import log.effect.internal.{ EffectSuspension, Id, Show }
import org.{ log4s => l4s }

object SyncLogWriter {

  import instances._

  def log4sLog[F[_]](l: l4s.Logger)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.pure(l))

  def log4sLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(l4s.getLogger(c)))

  def log4sLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(l4s.getLogger(n)))

  def julLog[F[_]](l: jul.Logger)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.pure(l))

  def julLog[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(jul.Logger.getGlobal))

  def scribeLog[F[_]](l: scribe.Logger)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.pure(l))

  def scribeLog[F[_]](n: String)(implicit F: Sync[F]): F[LogWriter[F]] =
    LogWriter.of(F.delay(scribe.Logger(n)))

  def scribeLog[F[_]](c: Class[_])(implicit F: Sync[F]): F[LogWriter[F]] = {
    import scribe._
    LogWriter.of(F.delay(c.logger))
  }

  def consoleLog[F[_]: Sync]: LogWriter[F] =
    LogWriter.from[Id].runningEffect[F](LogLevels.Trace)

  def consoleLogUpToLevel[F[_]: Sync, LL <: LogLevel](minLevel: LL): LogWriter[F] =
    LogWriter.from[Id].runningEffect[F](minLevel)

  val noOpLog: LogWriter[Id] =
    LogWriter.of[Id](())

  def noOpLogF[F[_]: Applicative]: LogWriter[F] =
    noOpLog.liftF

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

    implicit final class NoOpLogF(private val `_`: LogWriter[Id]) extends AnyVal {
      def liftF[F[_]: Applicative]: LogWriter[F] =
        new LogWriter[F] {
          def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): F[Unit] = Applicative[F].unit
        }
    }
  }
}
