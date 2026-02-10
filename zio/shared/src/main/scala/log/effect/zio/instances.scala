/*
 * Copyright (c) 2018-2025 LaserDisc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package log.effect
package zio

import _root_.zio.{LogLevel => _, _}
import log.effect.internal.{EffectSuspension, Id, Show}

import java.util.{logging => jul}

object instances {
  final case class LogName(x: String) extends AnyVal

  final type ZLogName      = LogName
  final type ZLogClass[A]  = Class[A]
  final type ZJulLogger    = jul.Logger
  final type ZScribeLogger = scribe.Logger
  final type ZLogWriter    = LogWriter[Task]

  private[zio] implicit final val taskEffectSuspension: EffectSuspension[Task] =
    new EffectSuspension[Task] {
      def suspend[A](a: =>A): Task[A] = ZIO.attempt(a)
    }

  private[zio] implicit final val uioEffectSuspension: EffectSuspension[UIO] =
    new EffectSuspension[UIO] {
      def suspend[A](a: =>A): UIO[A] = ZIO.succeed(a)
    }

  private[zio] implicit final def functorInstances[
    R,
    E
  ]: log.effect.internal.Functor[ZIO[R, E, *]] =
    new log.effect.internal.Functor[ZIO[R, E, *]] {
      def fmap[A, B](f: A => B): ZIO[R, E, A] => ZIO[R, E, B] = _ map f
    }

  implicit final class NoOpLogT(private val _underlying: LogWriter[Id]) extends AnyVal {
    def liftT: ZLogWriter =
      new LogWriter[Task] {
        override def write[A: Show](level: LogLevel, a: =>A): Task[Unit] = ZIO.unit
      }
  }
}
