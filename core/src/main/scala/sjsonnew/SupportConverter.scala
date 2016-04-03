package sjsonnew

trait SupportConverter[J] {
  def facade: Facade[J]
  def makeBuilder: Builder[J] = new Builder()

  def toJson[A](obj: A)(implicit writer: JsonWriter[A]): J =
    {
      val builder = makeBuilder
      writer.write(obj, builder, facade)
      builder.result match {
        case Some(r) => r
        case _       => facade.jnull()
      }
    }

  def fromJson[A](js: J)(implicit reader: JsonReader[A]): A =
    reader.read[J](js, facade)
}
