# fpmax-extended

This project provides my implementation of the FP to the Max workshop (https://www.youtube.com/watch?v=sxudIMiOo68) by John de Goes (https://twitter.com/jdegoes/). It extends the workshop by providing three recommendations given by John in the Q&A given in the end:

1. The Program typeclass is basically the Monad typeclass from any of the de facto FP libraries in Scala. I switched it by using the cats library (https://github.com/typelevel/cats).

2. The IO in the workshop is a dummy IO implementation used for demonstration purposes only. To show that the tagless-final and typeclasses facilitate the interchangeability between effect types, I created implementations for both both ZIO and cats-effect. (https://github.com/scalaz/scalaz-zio && https://github.com/typelevel/cats-effect)

3. The TestIO implementation done to explain how tests could be implemented is basically the State monad in disguise. I implemented instances for Random and Console using the State monad from cats and also provide some tests using ScalaTest (https://github.com/scalatest)
