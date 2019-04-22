import com.github.ghik.silencer.silent
import org.scalatest.{ Matchers, WordSpecLike }

@silent final class ReadmeConstructionCodeSnippetsTest extends WordSpecLike with Matchers {

  "Cats effect Sync construction snippets should compile" in {

    import java.util.{ logging => jul }

    import cats.effect.Sync
    import log.effect.fs2.SyncLogWriter._
    import log.effect.internal.Id
    import log.effect.{ LogLevels, LogWriter }
    import org.{ log4s => l4s }

    sealed abstract class App[F[_]](implicit F: Sync[F]) {

      val log4s1: F[LogWriter[F]] = log4sLog(F.delay(l4s.getLogger("a logger")))

      val log4s2: F[LogWriter[F]] = log4sLog("a logger")

      val log4s3: F[LogWriter[F]] = {
        case class LoggerClass()
        log4sLog(classOf[LoggerClass])
      }

      val jul1: F[LogWriter[F]] = julLog(F.delay(jul.Logger.getLogger("a logger")))

      val jul2: F[LogWriter[F]] = julLog

      val scribe1: F[LogWriter[F]] = scribeLog(F.delay(scribe.Logger("a logger")))

      val scribe2: F[LogWriter[F]] = scribeLog("a logger")

      val scribe3: F[LogWriter[F]] = {
        case class LoggerClass()
        scribeLog(classOf[LoggerClass])
      }

      val console1: LogWriter[F] = consoleLog

      val console2: LogWriter[F] = consoleLogUpToLevel(LogLevels.Warn)

      val noOp: LogWriter[Id] = noOpLog
    }
  }

  "Fs2 streams construction snippets should compile" in {

    import java.util.{ logging => jul }

    import cats.effect.Sync
    import log.effect.fs2.Fs2LogWriter._
    import log.effect.internal.Id
    import log.effect.{ LogLevels, LogWriter }
    import org.{ log4s => l4s }

    sealed abstract class App[F[_]](implicit F: Sync[F]) {

      val log4s1: fs2.Stream[F, LogWriter[F]] = log4sLogStream(F.delay(l4s.getLogger("a logger")))

      val log4s2: fs2.Stream[F, LogWriter[F]] = log4sLogStream("a logger")

      val log4s3: fs2.Stream[F, LogWriter[F]] = {
        case class LoggerClass()
        log4sLogStream(classOf[LoggerClass])
      }

      val jul1: fs2.Stream[F, LogWriter[F]] =
        julLogStream(F.delay(jul.Logger.getLogger("a logger")))

      val jul2: fs2.Stream[F, LogWriter[F]] = julLogStream

      val scribe1: fs2.Stream[F, LogWriter[F]] = scribeLogStream(F.delay(scribe.Logger("a logger")))

      val scribe2: fs2.Stream[F, LogWriter[F]] = scribeLogStream("a logger")

      val scribe3: fs2.Stream[F, LogWriter[F]] = {
        case class LoggerClass()
        scribeLogStream(classOf[LoggerClass])
      }

      val console1: fs2.Stream[F, LogWriter[F]] = consoleLogStream

      val console2: fs2.Stream[F, LogWriter[F]] = consoleLogStreamUpToLevel(LogLevels.Warn)

      val noOp: fs2.Stream[F, LogWriter[Id]] = noOpLogStream
    }
  }
}
