package com.github.lmreis.fpmax

import scala.io.StdIn.readLine
import scala.util.Try

object App1 {

  def parseInt(s: String): Option[Int] = {
    Try(s.toInt).toOption
  }

  def main: Unit = {
    println("What is your name?")

    val name = readLine()

    println("Hello, " + name + ", welcome to the game!")

    var exec = true

    while (exec) {
      val num = scala.util.Random.nextInt(5) + 1

      println("Dear " + name + ", please guess a number from 1 to 5:")

      val guess = parseInt(readLine())

      guess match {
        case None => println("You did not enter a number")
        case Some(guessedNum) =>
          if (guessedNum == num) println("You guessed right, " + name + "!")
          else println("You guessed wrong, " + name + "! The number was: " + num)
      }

      var cont = true

      while(cont) {
        cont = false
        println("Do you want to continue, " + name + "?")

        readLine().toLowerCase match {
          case "y" => exec = true
          case "n" => exec = false
          case _ => cont = true; println("Please choose between y or n")
        }
      }

    }
  }
}
