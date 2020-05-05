import com.github.ghik.silencer.silent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

@silent final class ReadmeConstructionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "Zio construction snippets should compile" in {
    import java.util.{logging => jul}

    import log.effect.zio.ZioLogWriter._
    import log.effect.{LogLevels, LogWriter}
    import org.{log4s => l4s}
    import zio.{RIO, Task}

    sealed abstract class App {
      def someZioProgramUsingLogs: RIO[LogWriter[Task], Unit]

      val log4s1: Task[Unit] =
        Task.effect(l4s.getLogger("a logger")) >>= { logger =>
          (log4sFromLogger >>> someZioProgramUsingLogs) provide logger
        }

      val log4s2: Task[Unit] =
        (log4sFromName >>> someZioProgramUsingLogs) provide "a logger name"

      val log4s3: Task[Unit] = {
        case class LoggerClass();
        (log4sFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
      }

      val jul1: Task[Unit] =
        Task.effect(jul.Logger.getLogger("a logger")) >>= { logger =>
          (julFromLogger >>> someZioProgramUsingLogs) provide logger
        }

      val jul2: Task[Unit] =
        julGlobal >>> someZioProgramUsingLogs

      val scribe1: Task[Unit] =
        Task.effect(scribe.Logger("a logger")) >>= { logger =>
          (scribeFromLogger >>> someZioProgramUsingLogs) provide logger
        }

      val scribe2: Task[Unit] =
        (scribeFromName >>> someZioProgramUsingLogs) provide "a logger name"

      val scribe3: Task[Unit] = {
        case class LoggerClass();
        (scribeFromClass >>> someZioProgramUsingLogs) provide classOf[LoggerClass]
      }

      val console1: Task[Unit] =
        someZioProgramUsingLogs provide consoleLog

      val console2: Task[Unit] =
        someZioProgramUsingLogs provide consoleLogUpToLevel(LogLevels.Warn)

      val noOp: Task[Unit] =
        someZioProgramUsingLogs provide noOpLog
    }
  }
}
