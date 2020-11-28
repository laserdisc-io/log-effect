package log.effect
package fs2
package mtl

import cats.data.ReaderT
import log.effect.internal.Show

object readerT {
  implicit def readerTLogWriter[F[_], Env](
    implicit LW: LogWriter[F]
  ): LogWriter[ReaderT[F, Env, *]] =
    new LogWriter[ReaderT[F, Env, *]] {
      def write[A: Show](level: LogLevel, a: =>A): ReaderT[F, Env, Unit] =
        ReaderT.liftF[F, Env, Unit](LW.write(level, a))
    }
}
