name := "FP to the MAX"

version := "0.1"

scalaVersion := "2.12.3"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.2.0",
  "org.typelevel" %% "cats-effect" % "0.10.1",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalaz" %% "scalaz-zio" % "0.1.0-0812841"
)
