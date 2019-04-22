package log
package effect
package fs2
package syntax

import _root_.fs2.Stream

import scala.language.implicitConversions

private[syntax] trait Fs2LogEffectSyntax extends Fs2LogEffectCompanionSyntax {

  implicit def fs2LogEffectSyntax[F[_]](aLogWriter: LogWriter[F]): Fs2LogEffectOps[F] =
    new Fs2LogEffectOps(aLogWriter)
}

sealed private[syntax] trait Fs2LogEffectCompanionSyntax {

  implicit def fs2LogEffectSyntaxSingleton[F[_]](`_`: LogWriter.type)(
    implicit LW: LogWriter[F]
  ): Fs2LogEffectOps[F] =
    new Fs2LogEffectOps(LW)
}

final private[syntax] class Fs2LogEffectOps[F[_]](private val aLogWriter: LogWriter[F])
    extends AnyVal {

  @inline def traceS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.trace(msg)

  @inline def traceS(th: =>Throwable)(implicit `_`: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.trace(th)

  @inline def traceS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.trace(msg, th)

  @inline def debugS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.debug(msg)

  @inline def debugS(th: =>Throwable)(implicit `_`: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.debug(th)

  @inline def debugS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.debug(msg, th)

  @inline def infoS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.info(msg)

  @inline def infoS(th: =>Throwable)(implicit `_`: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.info(th)

  @inline def infoS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.info(msg, th)

  @inline def errorS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.error(msg)

  @inline def errorS(th: =>Throwable)(implicit `_`: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.error(th)

  @inline def errorS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.error(msg, th)

  @inline def warnS(msg: =>String): Stream[F, Unit] =
    Stream eval aLogWriter.warn(msg)

  @inline def warnS(th: =>Throwable)(implicit `_`: DummyImplicit): Stream[F, Unit] =
    Stream eval aLogWriter.warn(th)

  @inline def warnS(msg: =>String, th: =>Throwable): Stream[F, Unit] =
    Stream eval aLogWriter.warn(msg, th)
}
