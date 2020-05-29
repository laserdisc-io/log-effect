import com.github.ghik.silencer.silent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

@silent("local method [a-zA-Z0-9]+ in value <local InteropTest> is never used")
final class InteropTest extends AnyWordSpecLike with Matchers {

  "A LogWriter instance can be derived from a log4cats Logger" in {
    import cats.effect.IO
    import cats.syntax.flatMap._
    import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
    import log.effect.LogWriter
    import log.effect.internal.Show
    import log.effect.interop.log4cats._

    final class A()
    object A {
      implicit val showA: Show[A] =
        new Show[A] {
          def show(a: A): String = "an A"
        }
    }

    implicit val buildMessageLogger = Slf4jLogger.fromName[IO]("from logger").unsafeRunSync()

    val logger = implicitly[LogWriter[IO]]

    val allLogs =
      logger.trace("a message") >>
        logger.trace(new Exception("an exception")) >>
        logger.trace("a message", new Exception("an exception")) >>
        logger.trace(new A()) >>
        logger.debug("a message") >>
        logger.debug(new Exception("an exception")) >>
        logger.debug("a message", new Exception("an exception")) >>
        logger.debug(new A()) >>
        logger.info("a message") >>
        logger.info(new Exception("an exception")) >>
        logger.info("a message", new Exception("an exception")) >>
        logger.info(new A()) >>
        logger.warn("a message") >>
        logger.warn(new Exception("an exception")) >>
        logger.warn("a message", new Exception("an exception")) >>
        logger.warn(new A()) >>
        logger.error("a message") >>
        logger.error(new Exception("an exception")) >>
        logger.error("a message", new Exception("an exception")) >>
        logger.error(new A())

    allLogs.unsafeRunSync()
  }

  "The readme interop example compiles" in {
    import cats.effect.{Resource, Sync}
    import cats.syntax.flatMap._
    import io.chrisdavenport.log4cats.Logger
    import log.effect.LogWriter

    import log.effect.interop.log4cats._

    sealed trait RedisClient[F[_]] {
      def address: String
      def write: F[Unit]
    }
    object RedisClient {
      def apply[F[_]: LogWriter](addr: String)(implicit F: Sync[F]): Resource[F, RedisClient[F]] =
        Resource.make(
          F.pure(
            new RedisClient[F] {
              val address        = addr
              def write: F[Unit] = LogWriter.info(address)
            }
          )
        )(_ => F.unit)
    }

    def buildLog4catsLogger[F[_]]: F[Logger[F]] = ???

    def storeOwnAddress[F[_]: Sync](address: String): F[Unit] =
      buildLog4catsLogger[F] >>= { implicit l =>
        RedisClient[F](address).use { cl =>
          cl.write
        }
      }
  }
}
