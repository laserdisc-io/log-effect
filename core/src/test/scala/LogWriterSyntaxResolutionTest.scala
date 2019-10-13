import com.github.ghik.silencer.silent
import log.effect.internal.Show
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class LogWriterSyntaxResolutionTest extends AnyWordSpecLike with Matchers {

  case class A()
  object A {
    implicit val aShow: Show[A] = new Show[A] {
      override def show(t: A): String = ???
    }
  }

  "the LogWriter's syntax" should {

    "be inferred without extra import" in {

      import log.effect.LogWriter

      @silent def test[F[_]](l: LogWriter[F]) = {

        l.trace(A())
        l.trace("test")
        l.trace(new Throwable("test"))
        l.trace("test", new Throwable("test"))

        l.debug(A())
        l.debug("test")
        l.debug(new Throwable("test"))
        l.debug("test", new Throwable("test"))

        l.info(A())
        l.info("test")
        l.info(new Throwable("test"))
        l.info("test", new Throwable("test"))

        l.error(A())
        l.error("test")
        l.error(new Throwable("test"))
        l.error("test", new Throwable("test"))

        l.warn(A())
        l.warn("test")
        l.warn(new Throwable("test"))
        l.warn("test", new Throwable("test"))
      }
    }
  }

  "the LogWriter's alias for the singleton companion" should {

    "be inferred allowing a boilerplate free mtl-style syntax" in {

      import log.effect.LogLevels.Trace
      import log.effect.LogWriter

      @silent def f1[F[_]: LogWriter] =
        LogWriter.write(Trace, "test")

      @silent def f2[F[_]: LogWriter] =
        LogWriter.trace(A())

      @silent def f3[F[_]: LogWriter] =
        LogWriter.debug(A())

      @silent def f4[F[_]: LogWriter] =
        LogWriter.info(A())

      @silent def f5[F[_]: LogWriter] =
        LogWriter.error(A())

      @silent def f6[F[_]: LogWriter] =
        LogWriter.warn(A())
    }

    "be inferred allowing a boilerplate free mtl-style syntax for errors" in {

      import log.effect.LogLevels.Error
      import log.effect.{ Failure, LogWriter }

      @silent def f1[F[_]: LogWriter] =
        LogWriter.write(Error, Failure("test", new Exception("test exception")))

      @silent def f2[F[_]: LogWriter] =
        LogWriter.error(Failure("test", new Exception("test exception")))
    }
  }
}
