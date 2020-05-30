import cats.effect.{IO, Resource, Sync}
import log.effect.LogWriter
import log.effect.fs2.{LogSelector, TestLogCapture}
import log.effect.fs2.SyncLogWriter.consoleLog
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class LogSelectorTest extends AnyWordSpecLike with Matchers with TestLogCapture {

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
            val useIt   = LogSelector[F].log.info("a test")
          }
        )
      )(_ => F.unit)
  }

  "Log selector uses the LogWriter if one is provided in scope" in {
    def useTheClient[F[_]: Sync](address: String): F[Unit] = {
      implicit val logger: LogWriter[F] = consoleLog[F]
      ALoggingClient[F](address).use(cl => cl.useIt)
    }

    val out = capturedConsoleOutOf(useTheClient[IO]("an address"))

    out should include("a test")
  }

  "Log selector defaults to noOpLog if no LogWriter is provided" in {
    def useTheClient[F[_]: Sync](address: String): F[Unit] =
      ALoggingClient[F](address).use(cl => cl.useIt)

    val out = capturedConsoleOutOf(useTheClient[IO]("an address"))

    out shouldBe empty
  }
}
