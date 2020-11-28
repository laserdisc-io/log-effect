import com.github.ghik.silencer.silent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

@silent final class ReadmeLayerConstructionCodeSnippetsTest extends AnyWordSpecLike with Matchers {
  "Zio Layer construction snippets should compile" in {
    import java.util.{logging => jul}

    import log.effect.zio.ZioLogWriter._
    import org.{log4s => l4s}
    import zio._

    object Setup {
      private val aLogName      = "a logger"
      private val aLog4sLogger  = l4s.getLogger("a log4s logger")
      private val aJulLogger    = jul.Logger.getLogger("a jul logger")
      private val aScribeLogger = scribe.Logger("a Scribe logger")

      final case class LoggerClass()
      final case class AConfig(logName: String)

      final val aConfigLive: ULayer[ZEnv with Has[AConfig]] =
        ZEnv.live ++ ZLayer.succeed(AConfig(aLogName))

      final val logNameLive: ULayer[ZEnv with ZLogName] =
        ZEnv.live ++ ZLayer.succeed(LogName(aLogName))

      final val log4sLoggerLive: ULayer[ZEnv with ZLog4sLogger] =
        ZEnv.live ++ ZLayer.succeed(aLog4sLogger)

      final val julLoggerLive: ULayer[ZEnv with ZJulLogger] =
        ZEnv.live ++ ZLayer.succeed(aJulLogger)

      final val scribeLoggerLive: ULayer[ZEnv with ZScribeLogger] =
        ZEnv.live ++ ZLayer.succeed(aScribeLogger)

      final val classLive: ULayer[ZEnv with ZLogClass[LoggerClass]] =
        ZEnv.live ++ ZLayer.succeed(classOf[LoggerClass])
    }

    sealed abstract class App {
      import Setup._

      // Case 1: from a possible config
      val logNameLiveFromConfig: ULayer[ZLogName] =
        aConfigLive >>> ZLayer.fromFunctionM { env =>
          ZIO.succeed(LogName(env.get[AConfig].logName))
        }

      val log4sCase1: TaskLayer[ZLogWriter] =
        logNameLiveFromConfig >>> log4sLayerFromName

      val scribeCase1: TaskLayer[ZLogWriter] =
        logNameLiveFromConfig >>> scribeLayerFromName

      // Case 2: from a name
      val log4sCase2: TaskLayer[ZLogWriter] =
        logNameLive >>> log4sLayerFromName

      val scribeCase2: TaskLayer[ZLogWriter] =
        logNameLive >>> scribeLayerFromName

      // Case 3: from a logger
      val log4sCase3: TaskLayer[ZLogWriter] =
        log4sLoggerLive >>> log4sLayerFromLogger

      val julCase3: TaskLayer[ZLogWriter] =
        julLoggerLive >>> julLayerFromLogger

      val scribeCase3: TaskLayer[ZLogWriter] =
        scribeLoggerLive >>> scribeLayerFromLogger

      // Case 4: from a class
      val log4sCase4: TaskLayer[ZLogWriter] =
        classLive >>> log4sLayerFromClass

      val scribeCase4: TaskLayer[ZLogWriter] =
        classLive >>> scribeLayerFromClass
    }
  }
}
