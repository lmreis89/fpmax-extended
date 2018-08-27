package com.github.lmreis.fpmax

import scala.io.StdIn.readLine
import scala.util.Try
import scala.language.higherKinds

object App3 {

  trait Program[F[_]] {
    def finish[A](a: => A): F[A]
    def chain[A, B](fa: F[A], afb: A => F[B]): F[B]
    def map[A, B](fa: F[A], ab: A => B): F[B]
  }

  object Program {
    def apply[F[_]](implicit F: Program[F]) = F
  }

  implicit class ProgramSyntax[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit F: Program[F]): F[B] = F.map(fa, f)
    def flatMap[B](afb: A => F[B])(implicit F: Program[F]): F[B] = F.chain(fa, afb)
  }

  trait Console[F[_]] {
    def putStrLn(line: String): F[Unit]
    def getStrLn: F[String]
  }

  object Console {
    def apply[F[_]](implicit F: Console[F]) = F
  }

  trait Random[F[_]] {
    def nextInt(ceiling: Int): F[Int]
  }

  object Random {
    def apply[F[_]](implicit F: Random[F]) = F
  }

  case class IO[A](unsafeRun: () => A) { self =>
    def map[B](f: A => B): IO[B] = IO(() => f(self.unsafeRun()))
    def flatMap[B](f: A => IO[B]): IO[B] = IO(() => f(self.unsafeRun()).unsafeRun())
  }

  object IO {
    def point[A](a: => A): IO[A] = IO(() => a)

    implicit val ProgramIO: Program[IO] = new Program[IO] {
      def finish[A](a: => A): IO[A] = IO.point(a)

      def chain[A, B](fa: IO[A], afb: A => IO[B]): IO[B] = fa.flatMap(afb)

      def map[A, B](fa: IO[A], ab: A => B): IO[B] = fa.map(ab)
    }

    implicit val ConsoleIO: Console[IO] = new Console[IO] {
      def putStrLn(line: String): IO[Unit] = IO(() => println(line))
      def getStrLn: IO[String] = IO(() => readLine())
    }

    implicit val RandomIO: Random[IO] = new Random[IO] {
      def nextInt(ceiling: Int): IO[Int] = IO(() => scala.util.Random.nextInt(ceiling))
    }
  }

  case class TestData(input: List[String], output: List[String], nums: List[Int]) {
    def putStrln(line: String): (TestData, Unit) = (copy(output = line :: output), ())
    def getStrln: (TestData, String) = (copy(input = input.drop(1)), input.head)
    def nextInt(ceiling: Int): (TestData, Int) = (copy(nums = nums.drop(1)), nums.head)
    def showResults = output.reverse.mkString("\n")
  }

  case class TestIO[A](run: TestData => (TestData, A)) { self =>
    def map[B](ab: A => B): TestIO[B] = {
      TestIO(t => self.run(t) match { case (data, a) => (data, ab(a)) })
    }

    def flatMap[B](afb: A => TestIO[B]): TestIO[B] = {
      TestIO(t => self.run(t) match { case (data, a) => afb(a).run(data) })
    }

    def eval(t: TestData): TestData = run(t)._1
  }

  object TestIO {
    def point[A](a: => A): TestIO[A] = TestIO(t => (t, a))

    implicit val ProgramIO: Program[TestIO] = new Program[TestIO] {
      def finish[A](a: => A): TestIO[A] = TestIO.point(a)

      def chain[A, B](fa: TestIO[A], afb: A => TestIO[B]): TestIO[B] = fa.flatMap(afb)

      def map[A, B](fa: TestIO[A], ab: A => B): TestIO[B] = fa.map(ab)
    }

    implicit val ConsoleIO: Console[TestIO] = new Console[TestIO] {
      def putStrLn(line: String): TestIO[Unit] = TestIO(t => t.putStrln(line))
      def getStrLn: TestIO[String] = TestIO(t => t.getStrln)
    }

    implicit val RandomIO: Random[TestIO] = new Random[TestIO] {
      def nextInt(ceiling: Int): TestIO[Int] = TestIO(t => t.nextInt(ceiling))
    }
  }

  def finish[F[_], A](a: => A)(implicit F: Program[F]): F[A] = F.finish(a)
  def getStrLn[F[_]: Console]: F[String] = Console[F].getStrLn
  def putStrLn[F[_]: Console](line: String): F[Unit] = Console[F].putStrLn(line)
  def nextInt[F[_]](ceiling: Int)(implicit F: Random[F]): F[Int] = Random[F].nextInt(ceiling)
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  def gameLoop[F[_]: Program: Console: Random](name: String): F[Unit] = {
    for {
      num <- nextInt(5).map(_ + 1)
      _ <- putStrLn("Dear " + name + ", please guess a number from 1 to 5:")
      input <- getStrLn
      _ <- parseInt(input).fold(
        putStrLn("You did not enter a number")
      )(guess =>
        if (guess == num) putStrLn("You guessed right, " + name + "!")
        else putStrLn("You guessed wrong, " + name + "! The number was: " + num)
      )
      cont <- checkContinue(name)
      _ <- if(cont) gameLoop(name) else finish(())
    } yield ()
  }

  def checkContinue[F[_]: Program: Console](name: String): F[Boolean] = {
    for {
      _ <- putStrLn("Do you want to continue, " + name + "?")
      input <- getStrLn.map(_.toLowerCase)
      cont <- input match {
        case "y" => finish(true)
        case "n" => finish(false)
        case _ => checkContinue(name)
      }
    } yield cont
  }

  def main[F[_]: Program: Console: Random]: F[Unit] = {
    for {
      _ <- putStrLn("What is your name?")
      name <- getStrLn
      _ <- putStrLn("Hello, " + name + ", welcome to the game!")
      _ <- gameLoop(name)
    } yield ()
  }

  def mainIO: IO[Unit] = main[IO]

  def mainTestIO: TestIO[Unit] = main[TestIO]

  val TestExample =
    TestData(
      input  = "John" :: "1" :: "n" :: Nil,
      output = Nil,
      nums   = 0 :: Nil
    )

  def runTest = mainTestIO.eval(TestExample).showResults

}
