package log
package effect
package fs2

import java.util.{ logging => jul }

import _root_.fs2.Stream
import cats.Applicative
import cats.effect.Sync
import log.effect.fs2.SyncLogWriter._
import log.effect.internal.Id
import org.{ log4s => l4s }

object Fs2LogWriter {
  def log4sLogStream[F[_]: Sync](l: l4s.Logger): Stream[F, LogWriter[F]] =
    Stream eval log4sLog(l)

  def log4sLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]] =
    Stream eval log4sLog(c)

  def log4sLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval log4sLog(n)

  def julLogStream[F[_]: Sync](l: jul.Logger): Stream[F, LogWriter[F]] =
    Stream eval julLog(l)

  def julLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream eval julLog

  def scribeLogStream[F[_]: Sync](l: scribe.Logger): Stream[F, LogWriter[F]] =
    Stream eval scribeLog(l)

  def scribeLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]] =
    Stream eval scribeLog(c)

  def scribeLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval { scribeLog(n) }

  def consoleLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream emit consoleLog

  def consoleLogStreamUpToLevel[F[_]: Sync, LL <: LogLevel](minLevel: LL): Stream[F, LogWriter[F]] =
    Stream emit consoleLogUpToLevel(minLevel)

  def noOpLogStream[F[_]]: Stream[F, LogWriter[Id]] =
    Stream emit noOpLog

  def noOpLogStreamF[F[_]: Applicative]: Stream[F, LogWriter[F]] =
    Stream emit noOpLogF
}
