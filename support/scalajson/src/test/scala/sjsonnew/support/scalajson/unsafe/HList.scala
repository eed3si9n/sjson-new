package sjsonnew
package support.scalajson
package unsafe

sealed trait HList {
  override def toString = this match {
    case HNil        => "HNil"
    case HCons(h, t) => s"($h) :+: $t"
  }
}

sealed trait HNil extends HList
object HNil extends HNil

final case class HCons[H, T <: HList](head: H, tail: T) extends HList

object HList {
  type :+:[H, T <: HList] = HCons[H, T]
  val :+: = HCons

  implicit class HNilOps(val _l: HNil) extends AnyVal {
    def :+:[H](h: H): H :+: HNil = HCons(h, _l)
  }

  implicit class HConsOps[H, T <: HList](val _l: HCons[H, T]) extends AnyVal {
    def :+:[G](g: G): G :+: H :+: T = HCons(g, _l)
  }

  implicit val lnilFormat1: JsonFormat[HNil] = forHNil(HNil)
  implicit val lnilFormat2: JsonFormat[HNil.type] = forHNil(HNil)

  private def forHNil[A <: HNil](hnil: A): JsonFormat[A] = new JsonFormat[A] {
    def write[J](x: A, builder: Builder[J]): Unit = {
      if (builder.state != BuilderState.InArray) builder.beginArray()
      builder.endArray()
    }

    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): A = {
      if (unbuilder.state == UnbuilderState.InArray) unbuilder.endArray()
      hnil
    }
  }

  implicit def hconsFormat[H, T <: HList](implicit hf: JsonFormat[H], tf: JsonFormat[T]): JsonFormat[H :+: T] =
    new JsonFormat[H :+: T] {
      def write[J](hcons: H :+: T, builder: Builder[J]) = {
        if (builder.state != BuilderState.InArray) builder.beginArray()
        hf.write(hcons.head, builder)
        tf.write(hcons.tail, builder)
      }

      def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]) = jsOpt match {
        case None => HCons(hf.read(None, unbuilder), tf.read(None, unbuilder))
        case Some(js) =>
          if (unbuilder.state != UnbuilderState.InArray) unbuilder.beginArray(js)
          HCons(hf.read(Some(unbuilder.nextElement), unbuilder), tf.read(Some(js), unbuilder))
      }
    }
}
