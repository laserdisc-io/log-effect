package log
package effect
package fs2
package mtl

import cats.data.ReaderT
import log.effect.internal.Show

object readerT {

  implicit def readerTLogWriter[F[_], Env](
    implicit LW: LogWriter[F]
  ): LogWriter[ReaderT[F, Env, *]] =
    new LogWriter[ReaderT[F, Env, *]] {

      def write[A: Show, L <: LogLevel: Show](level: L, a: =>A): ReaderT[F, Env, Unit] =
        ReaderT.liftF[F, Env, Unit](LW.write(level, a))
    }
}
