package com.github.lmreis.fpmax.extended

import scalaz.zio.IO
import Instances.ScalaZIO._

object ZIOApp extends scalaz.zio.App {
  def mainIO: IO[Throwable, Unit] = App.main[SyncIO]

  def run(args: List[String]) =
    mainIO.attempt.map(_.fold(_ => 1, _ => 0)).map(ExitStatus.ExitNow(_))
}
