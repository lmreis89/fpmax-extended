package com.github.lmreis.fpmax.extended

import org.scalatest.{Matchers, WordSpecLike}
import Instances.Test._

class AppTest extends WordSpecLike with Matchers {
  import com.github.lmreis.fpmax.extended.ConsoleOut._
  private val testMain = App.main[TestState]

  "The App" should {
    "handle the happy flow" in {
      val name = "John"
      val data = TestData(input = List(name, "1", "n"), output = List.empty , nums = List(0))
      val out = List(DoYouWantToContinue(name), YouGuessedRight(name), PleaseGuess(name), WelcomeToGame(name), WhatIsYourName)
      testMain.run(data).value._1 shouldEqual TestData(List.empty, out, List.empty)
    }

    "try twice" in {
      val name = "John"
      val data = TestData(input = List(name, "1", "y", "2", "n"), output = List.empty , nums = List(0, 1))
      val out = List(DoYouWantToContinue(name), YouGuessedRight(name), PleaseGuess(name), DoYouWantToContinue(name), YouGuessedRight(name), PleaseGuess(name), WelcomeToGame(name), WhatIsYourName)
      testMain.run(data).value._1 shouldEqual TestData(List.empty, out, List.empty)
    }

    "checks for continuation again if weird input is given" in {
      val name = "John"
      val data = TestData(input = List(name, "1", "nah", "n"), output = List.empty , nums = List(0))
      val out = List(DoYouWantToContinue(name), DoYouWantToContinue(name), YouGuessedRight(name), PleaseGuess(name), WelcomeToGame(name), WhatIsYourName)
      testMain.run(data).value._1 shouldEqual TestData(List.empty, out, List.empty)
    }

    "handles non-numbers and attempts to try again" in {
      val name = "John"
      val data = TestData(input = List(name, "one", "y", "1", "n"), output = List.empty , nums = List(0, 0))
      val out = List(DoYouWantToContinue(name), YouGuessedRight(name), PleaseGuess(name), DoYouWantToContinue(name), ThatIsNotValid(name), PleaseGuess(name), WelcomeToGame(name), WhatIsYourName)
      testMain.run(data).value._1 shouldEqual TestData(List.empty, out, List.empty)
    }
  }
}
