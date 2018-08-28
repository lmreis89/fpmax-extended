package com.github.lmreis.fpmax.extended

import cats.Monad
import cats.data.State
import cats.effect.{IO => CIO}
import com.github.lmreis.fpmax.extended.typeclasses.{Console, Random}
import scalaz.zio.{IO => ZIO}

import scala.io.StdIn.readLine

object Instances {
  object IO {
    implicit val ConsoleIO: Console[CIO] = new Console[CIO] {
      def putStrLn(line: ConsoleOut) : CIO[Unit] = {
        CIO(println(line.en))
      }

      def getStrLn: CIO[String] = {
        CIO(readLine())
      }
    }

    implicit val RandomIO: Random[CIO] = new Random[CIO] {
      def nextInt(ceiling: Int): CIO[Int] = CIO(scala.util.Random.nextInt(ceiling))
    }
  }

  object Test {
    case class TestData(input: List[String], output: List[ConsoleOut], nums: List[Int]) {
      def putStrLn(line: ConsoleOut): (TestData, Unit) = (copy(output = line :: output), ())
      def getStrLn: (TestData, String) = (copy(input = input.drop(1)), input.head)
      def nextInt(ceiling: Int): (TestData, Int) = (copy(nums = nums.drop(1)), nums.head)
      def showResults = output.reverse.mkString("\n")
    }

    type TestState[A] = State[TestData, A]

    implicit val ConsoleTest: Console[TestState] = new Console[TestState] {
      def putStrLn(line: ConsoleOut): TestState[Unit] = {
        State(t => t.putStrLn(line))
      }

      def getStrLn: TestState[String] = {
        State(t => t.getStrLn)
      }
    }

    implicit val RandomTest: Random[TestState] = new Random[TestState] {
      def nextInt(ceiling: Int): TestState[Int] = {
        State(t => t.nextInt(ceiling))
      }
    }
  }

  object ScalaZIO {
    type SyncIO[A] = ZIO[Throwable, A]

    implicit val MonadIO: Monad[SyncIO] = new Monad[SyncIO] {
      override def flatMap[A, B](fa: SyncIO[A])(f: A => SyncIO[B]): SyncIO[B] = fa.flatMap(f)

      override def tailRecM[A, B](a: A)(f: A => SyncIO[Either[A, B]]): SyncIO[B] = {
        f(a).flatMap {
          case Left(l)  => tailRecM(l)(f)
          case Right(r) => ZIO.now(r)
        }
      }

      override def pure[A](x: A): SyncIO[A] = ZIO.now(x)
    }

    implicit val ConsoleIO: Console[SyncIO] = new Console[SyncIO] {
      def putStrLn(line: ConsoleOut) : SyncIO[Unit] = {
        ZIO.sync(println(line.en))
      }

      def getStrLn: SyncIO[String] = {
        ZIO.sync(readLine())
      }
    }

    implicit val RandomIO: Random[SyncIO] = new Random[SyncIO] {
      def nextInt(ceiling: Int): SyncIO[Int] = ZIO.sync(scala.util.Random.nextInt(ceiling))
    }
  }
}
