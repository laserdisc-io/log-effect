package log.effect

import cats.Show
import cats.effect.Sync
import cats.syntax.show._
import com.github.ghik.silencer.silent
import log.effect.LogWriter.{ Console, NoOp }

trait LogWriterConstructor0[T, F[_]] {
  def evaluation: F[LogWriter[F]]
}

object LogWriterConstructor0 extends LogWriterConstructor0Instances {

  @inline final def apply[F[_]]: LogWriterConstructor0Partially[F] =
    new LogWriterConstructor0Partially()

  final private[effect] class LogWriterConstructor0Partially[F[_]](private val d: Boolean = true)
      extends AnyVal {

    @inline @silent def apply[T](t: T)(
      implicit F: Sync[F],
      LWC: LogWriterConstructor0[T, F]
    ): () => F[LogWriter[F]] =
      () => LWC.evaluation
  }
}

sealed private[effect] trait LogWriterConstructor0Instances {

  implicit def consoleConstructor0[F[_]](implicit F: Sync[F]): LogWriterConstructor0[Console, F] =
    new LogWriterConstructor0[Console, F] {
      def evaluation: F[LogWriter[F]] =
        F.pure(
          new LogWriter[F] {
            def write[A: Show](level: LogWriter.LogLevel, a: =>A): F[Unit] =
              F.delay {
                println(
                  s"[${level.show.toLowerCase}] - [${Thread.currentThread().getName}] ${a.show}"
                )
              }
          }
        )
    }

  implicit def noOpConstructor0[F[_]](implicit F: Sync[F]): LogWriterConstructor0[NoOp, F] =
    new LogWriterConstructor0[NoOp, F] {
      def evaluation: F[LogWriter[F]] =
        F.pure(
          new LogWriter[F] {
            def write[A: Show](level: LogWriter.LogLevel, a: =>A): F[Unit] =
              F.unit
          }
        )
    }
}
