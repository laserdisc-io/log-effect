package log.effect
package fs2

import cats.Applicative

/**
  * It helps adding logging capability to components
  * allowing them to use a `LogWriter[F]` if in scope
  * or defaulting to noOpLog if none is provided.
  *
  * Example:
  *
  * def create[F[_]: LogSelector](a: String): ALoggingClient[F] =
  *   new ALoggingClient[F] {
  *     def do = LogSelector[F].writer.info("something")
  *   }
  *
  * def useTheClient[F[_]](address: String): F[Unit] = {
  *   implicit val logger: LogWriter[F] = consoleLog[F]
  *   create[F](address)
  * }
  * will log using `logger`
  *
  * def useTheClient[F[_]](address: String): F[Unit] = create[F](address)
  * will not log
  */
final class LogSelector[F[_]](val log: LogWriter[F]) extends AnyVal

object LogSelector extends LogSelectorInstances0 {
  @inline final def apply[F[_]](implicit i: LogSelector[F]): LogSelector[F] = i
}

private[fs2] sealed trait LogSelectorInstances0 extends LogSelectorInstances1 {
  implicit def otherLogWriter[F[_]](implicit lw: LogWriter[F]): LogSelector[F] =
    new LogSelector(lw)
}

private[fs2] sealed trait LogSelectorInstances1 {
  implicit def noOpLogWriter[F[_]: Applicative]: LogSelector[F] =
    new LogSelector(SyncLogWriter.noOpLog[F])
}
