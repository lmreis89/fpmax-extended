package com.github.lmreis.fpmax.extended.typeclasses

import scala.language.higherKinds

trait Random[F[_]] {
  def nextInt(ceiling: Int): F[Int]
}

object Random {
  def apply[F[_]](implicit F: Random[F]) = F
}