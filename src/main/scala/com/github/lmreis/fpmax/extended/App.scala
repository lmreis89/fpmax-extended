package com.github.lmreis.fpmax.extended

import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.github.lmreis.fpmax.extended.typeclasses.{Console, Random}

import scala.language.higherKinds
import scala.util.Try

sealed trait ConsoleOut {
  def en: String
}
object ConsoleOut {
  case class YouGuessedRight(name: String) extends ConsoleOut {
    def en: String = "You guessed right, " + name + "!"
  }
  case class YouGuessedWrong(name: String, num: Int) extends ConsoleOut {
    def en: String = "You guessed wrong, " + name + "! The number was: " + num
  }
  case class DoYouWantToContinue(name: String) extends ConsoleOut {
    def en: String = "Do you want to continue, " + name + "?"
  }
  case class PleaseGuess(name: String) extends ConsoleOut {
    def en: String = "Dear " + name + ", please guess a number from 1 to 5:"
  }
  case class ThatIsNotValid(name: String) extends ConsoleOut {
    def en: String = "That is not a valid selection, " + name + "!"
  }
  case object WhatIsYourName extends ConsoleOut {
    def en: String = "What is your name?"
  }
  case class WelcomeToGame(name: String) extends ConsoleOut {
    def en: String = "Hello, " + name + ", welcome to the game!"
  }
}

object App {

  def finish[F[_], A](a: => A)(implicit F: Monad[F]): F[A] = F.point(a)
  def getStrLn[F[_]: Console]: F[String] = Console[F].getStrLn
  def putStrLn[F[_]: Console](line: ConsoleOut): F[Unit] = Console[F].putStrLn(line)
  def nextInt[F[_]](ceiling: Int)(implicit F: Random[F]): F[Int] = Random[F].nextInt(ceiling)
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  private def gameLoop[F[_]: Monad: Console: Random](name: String): F[Unit] = {
    for {
      num <- nextInt(5).map(_ + 1)
      _ <- putStrLn(ConsoleOut.PleaseGuess(name))
      input <- getStrLn
      _ <- parseInt(input).fold(
        putStrLn(ConsoleOut.ThatIsNotValid(name))
      )(guess =>
        if (guess == num) putStrLn(ConsoleOut.YouGuessedRight(name))
        else putStrLn(ConsoleOut.YouGuessedWrong(name, num))
      )
      cont <- checkContinue(name)
      _ <- if(cont) gameLoop(name) else finish(())
    } yield ()
  }

  private def checkContinue[F[_]: Monad: Console](name: String): F[Boolean] = {
    for {
      _ <- putStrLn(ConsoleOut.DoYouWantToContinue(name))
      input <- getStrLn.map(_.toLowerCase)
      cont <- input match {
        case "y" => finish(true)
        case "n" => finish(false)
        case _ => checkContinue(name)
      }
    } yield cont
  }

  def main[F[_]: Monad: Console: Random]: F[Unit] = {
    for {
      _ <- putStrLn(ConsoleOut.WhatIsYourName)
      name <- getStrLn
      _ <- putStrLn(ConsoleOut.WelcomeToGame(name))
      _ <- gameLoop(name)
    } yield ()
  }
}
