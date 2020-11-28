import com.github.ghik.silencer.silent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

@silent final class ReadmeRioConstructionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "Zio RIO construction snippets should compile" in {
    import java.util.{logging => jul}

    import log.effect.zio.ZioLogWriter._
    import log.effect.{LogLevels, LogWriter}
    import org.{log4s => l4s}
    import zio._

    object Setup {
      final case class AConfig(logName: String)
      final val aLogName                                            = "a logger"
      final def someZioProgramUsingLogs: RIO[LogWriter[Task], Unit] = ???
    }

    sealed abstract class App {
      import Setup._

      // Case 1: from a possible config in a Layer (gives a Layer)
      val log4sCase1: RLayer[Has[AConfig], ZLogWriter] =
        ZLayer.fromServiceM { config =>
          log4sFromName.provide(config.logName)
        }
      val scribe4sCase1: RLayer[Has[AConfig], ZLogWriter] =
        ZLayer.fromServiceM { config =>
          scribeFromName.provide(config.logName)
        }

      // Case 2: from a name
      val log4sCase2: Task[Unit] =
        (log4sFromName >>> someZioProgramUsingLogs) provide aLogName

      val scribeCase2: Task[Unit] =
        (scribeFromName >>> someZioProgramUsingLogs) provide aLogName

      // Case 3: from a logger
      val log4sCase3: Task[Unit] =
        Task.effect(l4s.getLogger(aLogName)) >>= { logger =>
          (log4sFromLogger >>> someZioProgramUsingLogs) provide logger
        }
      val julCase3: Task[Unit] =
        Task.effect(jul.Logger.getLogger(aLogName)) >>= { logger =>
          (julFromLogger >>> someZioProgramUsingLogs) provide logger
        }
      val scribeCase3: Task[Unit] =
        Task.effect(scribe.Logger(aLogName)) >>= { logger =>
          (scribeFromLogger >>> someZioProgramUsingLogs) provide logger
        }

      // Case 4: from a class
      val log4sCase4: Task[Unit] = {
        case class LoggerClass();
        (log4sFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
      }
      val scribeCase4: Task[Unit] = {
        case class LoggerClass();
        (scribeFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
      }

      // Case 5 (Jul): from global logger object
      val julCase5: Task[Unit] =
        julGlobal >>> someZioProgramUsingLogs

      // Case 6: console logger
      val console1: Task[Unit] =
        someZioProgramUsingLogs provide consoleLog

      val console2: Task[Unit] =
        someZioProgramUsingLogs provide consoleLogUpToLevel(LogLevels.Warn)

      // Case 7: No-op logger
      val noOp: Task[Unit] =
        someZioProgramUsingLogs provide noOpLog
    }
  }
}
