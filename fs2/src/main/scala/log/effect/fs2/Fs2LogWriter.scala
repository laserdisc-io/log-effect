package log.effect
package fs2

import java.util.{ logging => jul }

import _root_.fs2.Stream
import cats.effect.Sync
import log.effect.fs2.SyncLogWriter._
import org.{ log4s => l4s }

object Fs2LogWriter {

  def log4sLogStream[F[_]: Sync](fa: F[l4s.Logger]): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(fa) }

  def log4sLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(c) }

  def log4sLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(n) }

  def julLogStream[F[_]: Sync](fa: F[jul.Logger]): Stream[F, LogWriter[F]] =
    Stream eval { julLog(fa) }

  def julLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream eval { julLog }

  def scribeLogStream[F[_]: Sync](fa: F[scribe.Logger]): Stream[F, LogWriter[F]] =
    Stream eval { scribeLog(fa) }

  def scribeLogStream[F[_]: Sync](c: Class[_])(
    implicit ev: Class[_] <:< scribe.Logger
  ): Stream[F, LogWriter[F]] =
    Stream eval { scribeLog(c) }

  def scribeLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval { scribeLog(n) }

  def consoleLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream emit consoleLog

  def consoleLogStreamUpToLevel[F[_]]: ConsoleLogStreamPartial[F] =
    new ConsoleLogStreamPartial[F]

  final private[fs2] class ConsoleLogStreamPartial[F[_]](private val d: Boolean = true)
      extends AnyVal {
    def apply[LL <: LogLevel](minLevel: LL)(implicit F: Sync[F]): Stream[F, LogWriter[F]] =
      Stream emit consoleLogUpToLevel[F](minLevel)
  }

  def noOpLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream emit noOpLog
}
