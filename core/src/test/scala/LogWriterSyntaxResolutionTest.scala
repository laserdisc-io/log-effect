import com.github.ghik.silencer.silent
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class LogWriterSyntaxResolutionTest extends AnyWordSpecLike with Matchers {

  "the LogWriter's syntax" should {

    "be inferred without extra import" in {

      import log.effect.LogWriter

      @silent def test[F[_]](l: LogWriter[F]) = {
        l.trace("test")
        l.trace("test", new Throwable("test"))

        l.debug("test")
        l.debug("test", new Throwable("test"))

        l.info("test")
        l.info("test", new Throwable("test"))

        l.error("test")
        l.error("test", new Throwable("test"))

        l.warn("test")
        l.warn("test", new Throwable("test"))
      }
    }
  }

  "the LogWriter's alias for the singleton companion" should {

    "be inferred allowing a boilerplate free mtl-style syntax" in {

      import log.effect.LogLevels.Trace
      import log.effect.LogWriter

      @silent def f[F[_]: LogWriter] =
        LogWriter.write(Trace, "test")
    }

    "be inferred allowing a boilerplate free mtl-style syntax for errors" in {

      import log.effect.LogLevels.Error
      import log.effect.{ Failure, LogWriter }

      @silent def f[F[_]: LogWriter] =
        LogWriter.write(Error, Failure("test", new Exception("test exception")))
    }
  }
}
