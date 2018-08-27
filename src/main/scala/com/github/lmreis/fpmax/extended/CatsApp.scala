package com.github.lmreis.fpmax.extended

import cats.effect.IO
import Instances.IO._

object CatsApp {
  def mainIO: IO[Unit] = App.main[IO]

  def main(args: Array[String]) = mainIO.unsafeRunSync()
}
