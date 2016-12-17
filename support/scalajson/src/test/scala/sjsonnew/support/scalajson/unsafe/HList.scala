package sjsonnew
package support.scalajson
package unsafe

sealed trait HList {
  import HList._
  override def toString = this match {
    case x: HNil     => "HNil"
    case HCons(h, t) => s"($h) :+: $t"
  }
}

object HList {
  type :+:[H, T <: HList] = HCons[H, T]
  val :+: = HCons

  // Hide the HHil to make sure that the return type of HNil is HNil, not HNil.type
  private val hnil0 = new HNil {}
  val HNil: HNil = hnil0
  sealed trait HNil extends HList

  final case class HCons[H, T <: HList](head: H, tail: T) extends HList

  private implicit def mkTuple2[A1, A2](value: A1 :+: A2 :+: HNil) =
    (value.head, value.tail.head)
  private implicit def mkTuple3[A1, A2, A3](value: A1 :+: A2 :+: A3 :+: HNil) =
    (value.head, value.tail.head, value.tail.tail.head)
  implicit class HListOps[T <: HList](val _l: T) extends AnyVal {
    def :+:[H](h: H): H :+: T = HCons(h, _l)
  }
  import BasicJsonProtocol._

  implicit def hlist1Format[A1: JsonFormat]: JsonFormat[A1 :+: HNil] =
    project[A1 :+: HNil, A1](
      (value: A1 :+: HNil) => { value.head },
      (a1: A1) => { HCons(a1, HNil) }
    )
  implicit def hlist2Format[A1: JsonFormat, A2: JsonFormat]: JsonFormat[A1 :+: A2 :+: HNil] =
    project[A1 :+: A2 :+: HNil, (A1, A2)](
      (value: A1 :+: A2 :+: HNil) => {
        val x0t = value
        val x1 = x0t.head
        val x1t = value.tail
        val x2 = x1t.head
        (x1, x2)
      },
      (t: (A1, A2) ) => { HCons(t._1, HCons(t._2, HNil)) }
    )
  implicit def hlist3Format[A1: JsonFormat, A2: JsonFormat, A3: JsonFormat]:
    JsonFormat[A1 :+: A2 :+: A3 :+: HNil] =
    project[A1 :+: A2 :+: A3 :+: HNil, (A1, A2, A3)](
      (value: A1 :+: A2 :+: A3 :+: HNil) => {
        val x0t = value
        val x1 = x0t.head
        val x1t = value.tail
        val x2 = x1t.head
        val x2t = x1t.tail
        val x3 = x2t.head
        (x1, x2, x3)
      },
      (t: (A1, A2, A3) ) => { HCons(t._1, HCons(t._2, HCons(t._3, HNil))) }
    )
}
