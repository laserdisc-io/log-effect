import com.github.ghik.silencer.silent
import org.scalatest.{ Matchers, WordSpecLike }

final class LogWriterResolutionTest extends WordSpecLike with Matchers {

  "the construction" should {

    "correctly infer a valid log4s constructor for ZIO" in {

      import log.effect.internal.{ EffectSuspension, Functor }
      import log.effect.zio.ZioLogWriter.log4sFromLogger
      import log.effect.{ LogWriter, LogWriterConstructor }
      import org.{ log4s => l4s }
      import scalaz.zio.{ IO, Task }

      def c: Task[l4s.Logger] => Task[LogWriter[Task]] = {
        implicit def F: EffectSuspension[Task] = ???
        implicit def FF: Functor[Task]         = ???
        implicitly[LogWriterConstructor[Task[l4s.Logger], Task, Task]].construction
      }

      @silent def l1: Task[LogWriter[Task]] =
        c(IO.effect(l4s.getLogger("test")))

      @silent def l2: Task[LogWriter[Task]] =
        log4sFromLogger.provide(l4s.getLogger("test"))
    }

    "correctly infer a valid jul constructor for IO" in {

      import java.util.{ logging => jul }

      import log.effect.internal.{ EffectSuspension, Functor }
      import log.effect.zio.ZioLogWriter.julFromLogger
      import log.effect.{ LogWriter, LogWriterConstructor }
      import scalaz.zio.{ IO, Task }

      def c: Task[jul.Logger] => Task[LogWriter[Task]] = {
        implicit def F: EffectSuspension[Task] = ???
        implicit def FF: Functor[Task]         = ???
        implicitly[LogWriterConstructor[Task[jul.Logger], Task, Task]].construction
      }

      @silent def l1: Task[LogWriter[Task]] =
        c(IO.effect(jul.Logger.getGlobal))

      @silent def l2: Task[LogWriter[Task]] =
        julFromLogger.provide(jul.Logger.getGlobal)
    }
  }

  "the LogWriterConstructor0 of IO" should {

    "correctly infer a valid console constructor for IO" in {

      import log.effect.internal.{ EffectSuspension, Id }
      import log.effect.zio.ZioLogWriter.{ console, consoleUpToLevel }
      import log.effect.{ LogLevel, LogLevels, LogWriter, LogWriterConstructor }
      import scalaz.zio.Task

      def c[L <: LogLevel]: L => LogWriter[Task] = {
        implicit def F: EffectSuspension[Task] = ???
        implicitly[LogWriterConstructor[L, Id, Task]].construction
      }

      @silent def l1: LogWriter[Task] = c(LogLevels.Trace)

      @silent def l2: LogWriter[Task] = console

      @silent def l3: LogWriter[Task] = c(LogLevels.Info)

      @silent def l4: LogWriter[Task] = consoleUpToLevel(LogLevels.Info)
    }

    "not be able to infer a no-op constructor for zio Task" in {

      """
        |import log.effect.internal.Id
        |import log.effect.{LogWriter, LogWriterConstructor}
        |import scalaz.zio.Task
        |
        |@silent def c: Unit => LogWriter[Task] =
        |  implicitly[LogWriterConstructor[Unit, Id, Task]].construction
      """.stripMargin shouldNot compile
    }
  }
}
