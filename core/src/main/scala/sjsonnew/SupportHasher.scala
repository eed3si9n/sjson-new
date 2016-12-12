package sjsonnew

import scala.util.Try

trait SupportHasher[J] {
  implicit def facade: BuilderFacade[J]
  def makeBuilder: Builder[J] = new Builder(facade)

  /**
    * Convert an object of type `A` to a JSON AST of type `J`.
    */
  def hash[A](obj: A)(implicit writer: HashWriter[A]): Try[J] =
    Try(hashUnsafe(obj)(writer))

  /**
    * Convert an object of type `A` to a JSON AST of type `J`.
    * This might fail by throwing an exception.
    */
  def hashUnsafe[A](obj: A)(implicit writer: HashWriter[A]): J =
    {
      val builder = makeBuilder
      writer.write(obj, builder)
      builder.result match {
        case Some(r) => r
        case _       => facade.jnull()
      }
    }
}
