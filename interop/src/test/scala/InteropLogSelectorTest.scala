import cats.effect.{IO, Resource, Sync}
import cats.syntax.flatMap._
import com.github.ghik.silencer.silent
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import log.effect.fs2.LogSelector
import log.effect.interop.TestLogCapture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.slf4j.Logger

@silent("parameter value l in anonymous function is never used")
final class InteropLogSelectorTest extends AnyWordSpecLike with Matchers with TestLogCapture {

  private[this] sealed trait ALoggingClient[F[_]] {
    def address: String
    def useIt: F[Unit]
  }
  private[this] object ALoggingClient {
    def apply[F[_]: LogSelector](a: String)(
      implicit F: Sync[F]
    ): Resource[F, ALoggingClient[F]] =
      Resource.make(
        F.pure(
          new ALoggingClient[F] {
            val address = a
            val useIt   = LogSelector[F].log.info("this is a test")
          }
        )
      )(_ => F.unit)
  }

  "Log selector can infer the correct log if a log4cats Logger is in scope" in {
    import log.effect.interop.log4cats._

    def buildLog4catsLogger[F[_]: Sync](logger: Logger): F[SelfAwareStructuredLogger[F]] =
      Slf4jLogger.fromSlf4j[F](logger)

    def useLoggingClient[F[_]: Sync](address: String)(logger: Logger): F[Unit] =
      buildLog4catsLogger[F](logger) >>= { implicit l =>
        ALoggingClient[F](address).use(_.useIt)
      }

    val logged = capturedLog4sOutOf(useLoggingClient[IO]("an address")).map(_.message)

    logged shouldBe Seq("this is a test")
  }

  "Log selector will default to no logs if no log4cats Logger is in scope" in {

    def buildLog4catsLogger[F[_]: Sync](logger: Logger): F[SelfAwareStructuredLogger[F]] =
      Slf4jLogger.fromSlf4j[F](logger)

    def useLoggingClient[F[_]: Sync](address: String)(logger: Logger): F[Unit] =
      buildLog4catsLogger[F](logger) >>= { _ =>
        ALoggingClient[F](address).use(_.useIt)
      }

    val logged = capturedLog4sOutOf(useLoggingClient[IO]("an address"))

    logged shouldBe Seq()
  }
}
