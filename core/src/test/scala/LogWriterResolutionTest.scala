import org.scalatest.{Matchers, WordSpecLike}

final class LogWriterResolutionTest extends WordSpecLike with Matchers {

  "the LogWriterConstructor1 of F" should {

    "correctly infer a valid log4s constructor for an F[_] given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriter
        |import log.effect.LogWriter.Log4s
        |import log.effect.LogWriterConstructor1
        |
        |def c[F[_]]: F[org.log4s.Logger] => F[LogWriter[F]] = {
        |  implicit val F: Sync[F] = ???
        |  LogWriterConstructor1[F](Log4s)
        |}
        |
        |def l[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = c(F.delay(org.log4s.getLogger("test")))
      """.stripMargin should compile
    }

    "correctly infer a valid jul constructor for an F[_] given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriter
        |import log.effect.LogWriter.Jul
        |import log.effect.LogWriterConstructor1
        |
        |def c[F[_]]: F[java.util.logging.Logger] => F[LogWriter[F]] = {
        |  implicit val F: Sync[F] = ???
        |  LogWriterConstructor1[F](Jul)
        |}
        |
        |def l[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = c(F.delay(java.util.logging.Logger.getGlobal))
      """.stripMargin should compile
    }

    "not infer a valid log4s constructor for an F[_] if there is no implicit evidence of Sync[F]" in {

      """
        |import log.effect.LogWriter.Log4s
        |import log.effect.LogWriterConstructor1
        |
        |def c[F[_]] = LogWriterConstructor1[F](Log4s)
      """.stripMargin shouldNot compile
    }

    "not infer a valid constructor for an unknown log type and an F[_] even given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriterConstructor1
        |
        |case object UnknownType
        |
        |def c[F[_]] = {
        |  implicit val F: Sync[F] = ???
        |  LogWriterConstructor1[F](UnknownType)
        |}
      """.stripMargin shouldNot compile
    }
  }

  "the LogWriterConstructor0 of F" should {

    "correctly infer a valid console constructor for an F[_] given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriter
        |import log.effect.LogWriter.Console
        |import log.effect.LogWriterConstructor0
        |
        |def c[F[_]]: () => F[LogWriter[F]] = {
        |  implicit val F: Sync[F] = ???
        |  LogWriterConstructor0[F](Console)
        |}
        |
        |def l[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = c()
      """.stripMargin should compile
    }

    "correctly infer a valid no-op constructor for an F[_] given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriter
        |import log.effect.LogWriter.NoOp
        |import log.effect.LogWriterConstructor0
        |
        |def c[F[_]]: () => F[LogWriter[F]] = {
        |  implicit val F: Sync[F] = ???
        |  LogWriterConstructor0[F](NoOp)
        |}
        |
        |def l[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = c()
      """.stripMargin should compile
    }
  }

  "the LogWriterConstructor1 of IO" should {

    "correctly infer a valid log4s constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter
        |import log.effect.LogWriter.Log4s
        |import log.effect.LogWriterConstructor1
        |
        |val c: IO[org.log4s.Logger] => IO[LogWriter[IO]] = LogWriterConstructor1[IO](Log4s)
        |val l: IO[LogWriter[IO]] = c(IO(org.log4s.getLogger("test")))
      """.stripMargin should compile
    }

    "correctly infer a valid jul constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter
        |import log.effect.LogWriter.Jul
        |import log.effect.LogWriterConstructor1
        |
        |val c: IO[java.util.logging.Logger] => IO[LogWriter[IO]] = LogWriterConstructor1[IO](Jul)
        |val l: IO[LogWriter[IO]] = c(IO(java.util.logging.Logger.getGlobal))
      """.stripMargin should compile
    }
  }

  "the LogWriterConstructor0 of IO" should {

    "correctly infer a valid console constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter
        |import log.effect.LogWriter.Console
        |import log.effect.LogWriterConstructor0
        |
        |val c: () => IO[LogWriter[IO]] = LogWriterConstructor0[IO](Console)
        |val l: IO[LogWriter[IO]] = c()
      """.stripMargin should compile
    }

    "correctly infer a valid no-op constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter
        |import log.effect.LogWriter.NoOp
        |import log.effect.LogWriterConstructor0
        |
        |val c: () => IO[LogWriter[IO]] = LogWriterConstructor0[IO](NoOp)
        |val l: IO[LogWriter[IO]] = c()
      """.stripMargin should compile
    }
  }
}