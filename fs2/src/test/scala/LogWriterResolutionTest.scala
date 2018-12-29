import org.scalatest.{ Matchers, WordSpecLike }

final class LogWriterResolutionTest extends WordSpecLike with Matchers {

  "the LogWriterConstructor1 of F" should {

    "correctly infer a valid log4s constructor for an F[_] given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriter.Log4s
        |import log.effect.{ LogWriter, LogWriterConstructor1 }
        |import log.effect.fs2.SyncLogWriter.log4sLog
        |import log.effect.internal.{ EffectSuspension, Functor }
        |
        |def c[F[_]]: F[org.log4s.Logger] => F[LogWriter[F]] = {
        |  implicit val F: EffectSuspension[F] = ???
        |  implicit val FF: Functor[F]         = ???
        |  LogWriterConstructor1[F](Log4s)
        |}
        |
        |def l[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = c(F.delay(org.log4s.getLogger("test")))
        |
        |def l1[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
        |  log4sLog(F.delay(org.log4s.getLogger("test")))
      """.stripMargin should compile
    }

    "correctly infer a valid jul constructor for an F[_] given an implicit evidence of Sync[F]" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogWriter.Jul
        |import log.effect.{ LogWriter, LogWriterConstructor1 }
        |import log.effect.fs2.SyncLogWriter.julLog
        |import log.effect.internal.{ EffectSuspension, Functor }
        |
        |def c[F[_]]: F[java.util.logging.Logger] => F[LogWriter[F]] = {
        |  implicit val F: EffectSuspension[F] = ???
        |  implicit val FF: Functor[F]         = ???
        |  LogWriterConstructor1[F](Jul)
        |}
        |
        |def l[F[_]](implicit F: Sync[F]): F[LogWriter[F]] = c(F.delay(java.util.logging.Logger.getGlobal))
        |
        |def l1[F[_]](implicit F: Sync[F]): F[LogWriter[F]] =
        |  julLog(F.delay(java.util.logging.Logger.getGlobal))
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
        |import log.effect.internal.EffectSuspension
        |import log.effect.fs2.SyncLogWriter.consoleLog
        |import log.effect.LogWriter.Console
        |import log.effect.LogWriterConstructor0
        |
        |def c[F[_]]: () => LogWriter[F] = {
        |  implicit val F: EffectSuspension[F] = ???
        |  LogWriterConstructor0[F](Console)
        |}
        |
        |def l[F[_]]: LogWriter[F] = c()
        |
        |def l1[F[_]: Sync]: LogWriter[F] = consoleLog[F]
      """.stripMargin should compile
    }

    "correctly infer a valid no-op constructor for an F[_] given an implicit evidence of Applicative[F]" in {

      """
        |import cats.Applicative
        |import log.effect.LogWriter.NoOp
        |import log.effect.{ LogWriter, LogWriterConstructor0 }
        |import log.effect.fs2.SyncLogWriter.noOpLog
        |import log.effect.internal.NoAction
        |
        |def c[F[_]]: () => LogWriter[F] = {
        |  implicit val F: NoAction[F] = ???
        |  LogWriterConstructor0[F](NoOp)
        |}
        |
        |def l[F[_]: Applicative]: LogWriter[F] = c()
        |
        |def l1[F[_]: Applicative]: LogWriter[F] = noOpLog[F]
      """.stripMargin should compile
    }

    "correctly infer a valid console constructor for an F[_] with a minimum level" in {

      """
        |import cats.effect.Sync
        |import log.effect.LogLevels.Info
        |import log.effect.LogWriter.Console
        |import log.effect.{LogWriter, LogWriterConstructor0}
        |import log.effect.fs2.SyncLogWriter.consoleLogUpToLevel
        |import log.effect.internal.EffectSuspension
        |
        |def c[F[_]]: () => LogWriter[F] = {
        |  implicit val F: EffectSuspension[F] = ???
        |  LogWriterConstructor0[F](Console, Info)
        |}
        |
        |def l[F[_]]: LogWriter[F] = c()
        |
        |def l1[F[_]: Sync]: LogWriter[F] = consoleLogUpToLevel(Info)
      """.stripMargin should compile
    }

    "correctly infer a valid no-op constructor for an F[_] with a minimum level" in {

      """
        |import log.effect.LogLevels.Info
        |import log.effect.LogWriter.NoOp
        |import log.effect.{ LogWriter, LogWriterConstructor0 }
        |import log.effect.internal.NoAction
        |
        |def c[F[_]]: () => LogWriter[F] = {
        |  implicit val F: NoAction[F] = ???
        |  LogWriterConstructor0[F](NoOp, Info)
        |}
        |
        |def l[F[_]]: LogWriter[F] = c()
      """.stripMargin should compile
    }
  }

  "the LogWriterConstructor1 of IO" should {

    "correctly infer a valid log4s constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter.Log4s
        |import log.effect.{ LogWriter, LogWriterConstructor1 }
        |import log.effect.fs2.SyncLogWriter.log4sLog
        |import log.effect.internal.{ EffectSuspension, Functor }
        |
        |{
        |  implicit val F: EffectSuspension[IO] = ???
        |  implicit val FF: Functor[IO]         = ???
        |
        |  val c: IO[org.log4s.Logger] => IO[LogWriter[IO]] =
        |    LogWriterConstructor1[IO](Log4s)
        |
        |  val l: IO[LogWriter[IO]] = c(IO(org.log4s.getLogger("test")))
        |}
        |
        |val l1: IO[LogWriter[IO]] = log4sLog(IO(org.log4s.getLogger("test")))
      """.stripMargin should compile
    }

    "correctly infer a valid jul constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter.Jul
        |import log.effect.{ LogWriter, LogWriterConstructor1 }
        |import log.effect.fs2.SyncLogWriter.julLog
        |import log.effect.internal.{ EffectSuspension, Functor }
        |
        |{
        |  implicit val F: EffectSuspension[IO] = ???
        |  implicit val FF: Functor[IO]         = ???
        |
        |  val c: IO[java.util.logging.Logger] => IO[LogWriter[IO]] =
        |    LogWriterConstructor1[IO](Jul)
        |
        |  val l: IO[LogWriter[IO]] = c(IO(java.util.logging.Logger.getGlobal))
        |}
        |
        |val l1: IO[LogWriter[IO]] = julLog(IO(java.util.logging.Logger.getGlobal))
      """.stripMargin should compile
    }
  }

  "the LogWriterConstructor0 of IO" should {

    "correctly infer a valid console constructor for IO" in {

      """
        |import cats.effect.IO
        |  import log.effect.LogWriter.Console
        |  import log.effect.{ LogWriter, LogWriterConstructor0 }
        |  import log.effect.fs2.SyncLogWriter.consoleLog
        |  import log.effect.internal.EffectSuspension
        |
        |  {
        |    implicit val F: EffectSuspension[IO] = ???
        |
        |    val c: () => LogWriter[IO] = LogWriterConstructor0[IO](Console)
        |    val l: LogWriter[IO]       = c()
        |  }
        |
        |  val l1: LogWriter[IO] = consoleLog[IO]
      """.stripMargin should compile
    }

    "correctly infer a valid no-op constructor for IO" in {

      """
        |import cats.effect.IO
        |import log.effect.LogWriter.NoOp
        |import log.effect.{ LogWriter, LogWriterConstructor0 }
        |import log.effect.fs2.SyncLogWriter.noOpLog
        |import log.effect.internal.NoAction
        |
        |{
        |  implicit val F: NoAction[IO] = ???
        |
        |  val c: () => LogWriter[IO] =
        |    LogWriterConstructor0[IO](NoOp)
        |
        |  val l: LogWriter[IO] = c()
        |}
        |
        |val l1: LogWriter[IO] = noOpLog[IO]
      """.stripMargin should compile
    }
  }

  "the LogWriter's mtl style" should {

    "infer the syntax methods when there is a LogWriter's evidence" in {
      """
        |import cats.effect.Sync
        |import cats.syntax.apply._
        |import log.effect.LogWriter
        |
        |def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] = {
        |
        |  source evalMap (
        |    n =>
        |      LogWriter.debug("Processing a number") *>
        |        LogWriter.debug(n.toString) *>
        |        Sync[F].pure(n * 2) <*
        |        LogWriter.debug("Processed")
        |  ) handleErrorWith (
        |    th =>
        |      fs2.Stream.eval(
        |        LogWriter.error("Ops, something didn't work", th) *> Sync[F].pure(0)
        |      )
        |  )
        |}
      """.stripMargin should compile
    }

    "infer the internal log when a cats.Show is present and the implicits are imported" in {
      """
        |import cats.Show
        |import cats.effect.Sync
        |import cats.syntax.apply._
        |import log.effect.LogLevels.{ Debug, Error }
        |import log.effect.LogWriter
        |import log.effect.Failure
        |import log.effect.fs2.implicits._
        |
        |def double[F[_]: Sync: LogWriter](source: fs2.Stream[F, Int]): fs2.Stream[F, Int] = {
        |
        |  // Show instances are needed for every logged type
        |  implicit def addressShow: Show[Int] = ???
        |
        |  source evalMap (
        |    n =>
        |      LogWriter.write(Debug, "Processing a number") *>
        |        LogWriter.write(Debug, n) *>
        |        Sync[F].pure(n * 2) <*
        |        LogWriter.write(Debug, "Processed")
        |  ) handleErrorWith (
        |    th =>
        |      fs2.Stream.eval(
        |        LogWriter.write(Error, Failure("Ops, something didn't work", th)) *> Sync[F].pure(0)
        |      )
        |  )
        |}
      """.stripMargin should compile
    }
  }
}
