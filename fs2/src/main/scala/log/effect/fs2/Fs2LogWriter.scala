package log.effect.fs2

import java.util.{logging => jul}

import cats.effect.Sync
import fs2.Stream
import log.effect.LogWriter
import log.effect.LogWriter._
import org.{log4s => l4s}

object Fs2LogWriter {

  def log4sLogStream[F[_]](c: Class[_])(implicit F: Sync[F]): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(F.delay(l4s.getLogger(c))) }

  def log4sLogStream[F[_]](n: String)(implicit F: Sync[F]): Stream[F, LogWriter[F]] =
    Stream eval { log4sLog(F.delay(l4s.getLogger(n))) }

  def julLogStream[F[_]](implicit F: Sync[F]): Stream[F, LogWriter[F]] =
    Stream eval { julLog(F.delay(jul.Logger.getGlobal)) }

  def consoleLogStream[F[_] : Sync]: Stream[F, LogWriter[F]] =
    Stream eval { consoleLog }
}