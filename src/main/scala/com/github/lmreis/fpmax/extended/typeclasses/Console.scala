package com.github.lmreis.fpmax.extended.typeclasses

import com.github.lmreis.fpmax.extended.ConsoleOut

import scala.language.higherKinds

trait Console[F[_]] {
  def putStrLn(line: ConsoleOut): F[Unit]
  def getStrLn: F[String]
}

object Console {
  def apply[F[_]](implicit F: Console[F]) = F
}
