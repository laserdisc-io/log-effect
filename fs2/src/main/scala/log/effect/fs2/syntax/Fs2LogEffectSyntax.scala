package log.effect.fs2.syntax

import fs2.Stream
import log.effect.LogWriter

import scala.language.implicitConversions

private[fs2] trait Fs2LogEffectSyntax {

  implicit def fs2LogEffectSyntax[F[_]](aLogWriter: LogWriter[F]): Fs2LogEffectOps[F] = new Fs2LogEffectOps(aLogWriter)
}

private[syntax] final class Fs2LogEffectOps[F[_]](private val aLogWriter: LogWriter[F]) extends AnyVal {

  @inline def traceS(msg: =>String): Stream[F, Unit]                  = Stream eval aLogWriter.trace(msg)
  @inline def traceS(msg: =>String, th: =>Throwable): Stream[F, Unit] = Stream eval aLogWriter.trace(msg, th)

  @inline def debugS(msg: =>String): Stream[F, Unit]                  = Stream eval aLogWriter.debug(msg)
  @inline def debugS(msg: =>String, th: =>Throwable): Stream[F, Unit] = Stream eval aLogWriter.debug(msg, th)

  @inline def infoS(msg: =>String): Stream[F, Unit]                   = Stream eval aLogWriter.info(msg)
  @inline def infoS(msg: =>String, th: =>Throwable): Stream[F, Unit]  = Stream eval aLogWriter.info(msg, th)

  @inline def errorS(msg: =>String): Stream[F, Unit]                  = Stream eval aLogWriter.error(msg)
  @inline def errorS(msg: =>String, th: =>Throwable): Stream[F, Unit] = Stream eval aLogWriter.error(msg, th)

  @inline def warnS(msg: =>String): Stream[F, Unit]                   = Stream eval aLogWriter.warn(msg)
  @inline def warnS(msg: =>String, th: =>Throwable): Stream[F, Unit]  = Stream eval aLogWriter.warn(msg, th)
}