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

  implicit class HNilOps(private val _l: HNil) extends AnyVal {
    def :+:[H](h: H): H :+: HNil = HCons(h, _l)
  }

  implicit class HConsOps[H, T <: HList](private val _l: HCons[H, T]) extends AnyVal {
    def :+:[G](g: G): G :+: H :+: T = HCons(g, _l)
  }

  implicit val lnilFormat1: JsonFormat[HNil] = forHNil(HNil)
  implicit val lnilFormat2: JsonFormat[HNil.type] = forHNil(HNil)

  private def forHNil[A <: HNil](hnil: A): JsonFormat[A] = new JsonFormat[A] {
    def write[J](x: A, builder: Builder[J]): Unit = {
      builder.beginArray()
      builder.endArray()
    }

    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): A = jsOpt match {
      case None     => hnil
      case Some(js) => unbuilder.beginArray(js); unbuilder.endArray(); hnil
    }
  }

  implicit def hconsFormat[H, T <: HList](implicit hf: JsonFormat[H], tf: HListJF[T]): JsonFormat[H :+: T] =
    new JsonFormat[H :+: T] {
      def write[J](hcons: H :+: T, builder: Builder[J]) = {
        builder.beginArray()
        hf.write(hcons.head, builder)
        tf.write(hcons.tail, builder)
        builder.endArray()
      }

      def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]) = jsOpt match {
        case None => HCons(hf.read(None, unbuilder), tf.read(None, unbuilder))
        case Some(js) =>
          unbuilder.beginArray(js)
          val hcons = HCons(hf.read(Some(unbuilder.nextElement), unbuilder), tf.read(Some(js), unbuilder))
          unbuilder.endArray()
          hcons
      }
    }

  trait HListJF[A <: HList] {
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): A
    def write[J](obj: A, builder: Builder[J]): Unit
  }

  implicit def hconsHListJF[H, T <: HList](implicit hf: JsonFormat[H], tf: HListJF[T]): HListJF[H :+: T] =
    new HListJF[H :+: T] {
      def write[J](hcons: H :+: T, builder: Builder[J]) = {
        hf.write(hcons.head, builder)
        tf.write(hcons.tail, builder)
      }

      def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]) = jsOpt match {
        case None     => HCons(hf.read(None, unbuilder), tf.read(None, unbuilder))
        case Some(js) => HCons(hf.read(Some(unbuilder.nextElement), unbuilder), tf.read(Some(js), unbuilder))
      }
    }

  implicit val lnilHListJF1: HListJF[HNil]      = hnilHListJF(HNil)
  implicit val lnilHListJF2: HListJF[HNil.type] = hnilHListJF(HNil)

  implicit def hnilHListJF[A <: HNil](hnil: A): HListJF[A] = new HListJF[A] {
    def write[J](hcons: A, builder: Builder[J]) = ()
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]) = hnil
  }
}
