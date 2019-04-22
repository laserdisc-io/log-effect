import com.github.ghik.silencer.silent
import org.scalatest.{ Matchers, WordSpecLike }

@silent final class ReadmeCodeSnippetsTest extends WordSpecLike with Matchers {

  "`in a monadic sequence of effects` snippet should compile" in {

    import cats.effect.Sync
    import cats.syntax.flatMap._
    import cats.syntax.functor._
    import log.effect.LogWriter

    def process[F[_]](implicit F: Sync[F], log: LogWriter[F]): F[(Int, Int)] =
      for {
        _ <- log.trace("We start")
        a <- F.delay(10)
        _ <- log.trace("Keep going")
        b <- F.delay(20)
        _ <- log.trace("We reached this point")
        _ <- log.info(s"Process complete: ${(a, b)}")
      } yield (a, b)
  }

  "`in a streaming environment using `LogWriter`'s syntax` snippet should compile" in {

    import java.nio.channels.AsynchronousChannelGroup

    import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
    import cats.syntax.flatMap._
    import laserdisc.fs2.{ RedisAddress, RedisClient }
    import log.effect.LogWriter

    import scala.concurrent.ExecutionContext

    implicit def EC: ExecutionContext         = ???
    implicit def CG: AsynchronousChannelGroup = ???

    def redisClient[F[_]: ConcurrentEffect: ContextShift: Timer](
      address: RedisAddress
    )(implicit log: LogWriter[F]): fs2.Stream[F, RedisClient[F]] =
      RedisClient[F](Set(address)) evalMap { client =>
        log.info(s"Laserdisc Redis client for $address") >> ConcurrentEffect[F].pure(client)
      }
  }

  "`in a streaming environment using `fs2` streams' syntax` snippet should compile" in {

    import java.nio.channels.AsynchronousChannelGroup

    import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
    import laserdisc.fs2.{ RedisAddress, RedisClient }
    import log.effect.LogWriter
    import log.effect.fs2.syntax._

    import scala.concurrent.ExecutionContext

    implicit def EC: ExecutionContext         = ???
    implicit def CG: AsynchronousChannelGroup = ???

    def redisCache[F[_]: ConcurrentEffect: ContextShift: Timer](
      address: RedisAddress
    )(implicit log: LogWriter[F]): fs2.Stream[F, RedisClient[F]] =
      for {
        _      <- log.infoS(s"About to connect a Laserdisc Redis client for $address")
        client <- RedisClient[F](Set(address))
        _      <- log.infoS("The connection went fine. Talking to the server")
      } yield client
  }

  "`through the companion's syntax` snippet should compile" in {

    import cats.effect.Sync
    import cats.syntax.apply._
    import cats.syntax.flatMap._
    import log.effect.LogWriter

    def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] =
      source evalMap { n =>
        LogWriter.debug("Processing a number") >>
          LogWriter.debug(n.toString) >>
          Sync[F].pure(n * 2) <*
          LogWriter.debug("Processed")
      } handleErrorWith { th =>
        fs2.Stream eval (
          LogWriter.error("Ops, something didn't work", th) >> Sync[F].pure(0)
        )
      }
  }

  "`through the companion's syntax for `fs2` streams` snippet should compile" in {

    import cats.effect.Sync
    import cats.syntax.apply._
    import cats.syntax.flatMap._
    import log.effect.LogWriter
    import log.effect.fs2.syntax._

    def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] =
      (source >>= { n =>
        LogWriter.debugS("Processing a number") >>
          LogWriter.debugS(n.toString) >>
          fs2.Stream.eval(Sync[F].pure(n * 2)) <*
          LogWriter.debugS("Processed")
      }) handleErrorWith { th =>
        LogWriter.errorS("Ops, something didn't work", th) >> fs2.Stream.eval(Sync[F].pure(0))
      }
  }

  "`through the companion's accessor to the `write` method` 1 snippet should compile" in {

    import java.nio.channels.AsynchronousChannelGroup

    import cats.Show
    import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
    import cats.instances.string._
    import cats.syntax.either._
    import cats.syntax.flatMap._
    import laserdisc.fs2.{ RedisAddress, RedisClient }
    import log.effect.LogLevels.{ Debug, Error }
    import log.effect.fs2.interop.show._
    import log.effect.{ Failure, LogWriter }

    import scala.concurrent.ExecutionContext

    type |[A, B] = Either[A, B]

    implicit def EC: ExecutionContext         = ???
    implicit def CG: AsynchronousChannelGroup = ???

    def redisClient[F[_]: ConcurrentEffect: ContextShift: Timer: LogWriter](
      address: RedisAddress
    ): fs2.Stream[F, Throwable | RedisClient[F]] = {

      // Cats Show instances are needed for every logged type
      implicit val addressShow: Show[RedisAddress]  = ???
      implicit val clientShow: Show[RedisClient[F]] = ???

      RedisClient[F](Set(address)) evalMap { client =>
        LogWriter.write(Debug, "Connected client details:") >>
          LogWriter.write(Debug, address) >>
          LogWriter.write(Debug, client) >>
          ConcurrentEffect[F].pure(client.asRight)
      } handleErrorWith { th =>
        fs2.Stream eval (
          LogWriter.write(Error, Failure("Ops, something didn't work", th)) >>
            ConcurrentEffect[F].pure(th.asLeft)
        )
      }
    }
  }

  "`through the companion's accessor to the `write` method` 2 snippet should compile" in {

    import cats.Show
    import cats.effect.Sync
    import cats.syntax.apply._
    import cats.syntax.flatMap._
    import log.effect.LogLevels.{ Debug, Error }
    import log.effect.fs2.interop.show._
    import log.effect.{ Failure, LogWriter }

    def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] = {

      // Cats Show instances are needed for every logged type
      implicit def intShow: Show[Int] = ???

      source evalMap { n =>
        LogWriter.write(Debug, "Processing a number") >>
          LogWriter.write(Debug, n) >>
          Sync[F].pure(n * 2) <*
          LogWriter.write(Debug, "Processed")
      } handleErrorWith { th =>
        fs2.Stream eval (
          LogWriter.write(Error, Failure("Ops, something didn't work", th)) >> Sync[F].pure(0)
        )
      }
    }
  }
}
