package sjsonnew

import scala.util.Try

trait SupportConverter[J] {
  implicit def facade: Facade[J]
  def makeBuilder: Builder[J] = new Builder(facade)
  def makeUnbuilder: Unbuilder[J] = new Unbuilder(facade)

  /**
    * Convert an object of type `A` to a JSON AST of type `J`.
    */
  def toJson[A](obj: A)(implicit writer: JsonWriter[A]): Try[J] =
    Try(toJsonUnsafe(obj)(writer))

  /**
    * Convert an object of type `A` to a JSON AST of type `J`.
    * This might fail by throwing an exception.
    */
  def toJsonUnsafe[A](obj: A)(implicit writer: JsonWriter[A]): J =
    {
      val builder = makeBuilder
      writer.write(obj, builder)
      builder.result match {
        case Some(r) => r
        case _       => facade.jnull()
      }
    }

  /**
    * Convert a JSON AST of type `J` to an object of type `A`.
    */
  def fromJson[A](js: J)(implicit reader: JsonReader[A]): Try[A] =
    Try(fromJsonOptionUnsafe[A](Some(js))(reader))

  /**
    * Convert a JSON AST of type `J` to an object of type `A`.
    * This might fail by throwing an exception.
    */
  def fromJsonUnsafe[A](js: J)(implicit reader: JsonReader[A]): A =
    fromJsonOptionUnsafe(Some(js))(reader)

  /**
    * Convert a JSON AST of type `J` to an object of type `A`.
    * This might fail by throwing an exception.
    */
  def fromJsonOptionUnsafe[A](jsOpt: Option[J])(implicit reader: JsonReader[A]): A =
    {
      val unbuilder = makeUnbuilder
      reader.read[J](jsOpt, unbuilder)
    }
}
