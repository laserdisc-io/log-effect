package log.effect
package fs2

import java.util.{ logging => jul }

import _root_.fs2.Stream
import cats.effect.Sync
import log.effect.LogWriter._

object Fs2LogWriter {

  def log4sLogStream[F[_]: Sync](c: Class[_]): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(c) }

  def log4sLogStream[F[_]: Sync](n: String): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(n) }

  def julLogStream[F[_]](implicit F: Sync[F]): Stream[F, LogWriter[F]] =
    Stream eval { julLog(F.delay(jul.Logger.getGlobal)) }

  def consoleLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream(consoleLog)

  def noOpLogStream[F[_]: Sync]: Stream[F, LogWriter[F]] =
    Stream(noOpLog)
}
