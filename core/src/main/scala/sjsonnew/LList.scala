/*
 * Copyright (C) 2016 Eugene Yokota
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sjsonnew

import scala.reflect.ClassTag

/** Heterogeneous list (of json serializable stuff) with labels. */
sealed trait LList {
  def find[A1: ClassTag](n: String): Option[A1]
  def fieldNames: List[String]
}

object LList extends LListFormats {
  @deprecated("Switch to the type alias in the sjsonnew root package", "0.8.0")
  type :*:[A1, A2 <: LList] = LCons[A1, A2]
  final val :*: = sjsonnew.:*:

  def iso[A, R0 <: LList: JsonFormat](to0: A => R0, from0: R0 => A): IsoLList.Aux[A, R0] =
    IsoLList.iso[A, R0](to0, from0)

  /** Curried iso for type inference. */
  def isoCurried[A, R0 <: LList: JsonFormat](to0: A => R0)(from0: R0 => A): IsoLList.Aux[A, R0] =
    IsoLList.iso[A, R0](to0, from0)

  // This is so the return type of LNil becomes LNil, instead of LNil.type.
  val LNil0: LNil0 = new LNil0 {}
  sealed trait LNil0 extends LList {
    def :*:[A1: JsonFormat: ClassTag](labelled: (String, A1)): LCons[A1, LNil] = LCons(labelled._1, labelled._2, this)

    override def toString: String = "LNil"
    override def find[A1: ClassTag](n: String): Option[A1] = None
    override def fieldNames: List[String] = Nil
  }

  implicit def llistOps[L <: LList](l: L): LListOps[L] = new LListOps(l)
}

final class LCons[A1: JsonFormat: ClassTag, A2 <: LList: JsonFormat](
    val name: String,
    val head: A1,
    val tail: A2) extends LList with Product with Serializable with Equals {
  def :*:[B1: JsonFormat: ClassTag](labelled: (String, B1)): B1 :*: A1 :*: A2 = LCons(labelled._1, labelled._2, this)
  override def toString: String = s"($name, $head) :*: $tail"
  override def productPrefix: String = "LCons"
  override def find[B1: ClassTag](n: String): Option[B1] =
    if (name == n && implicitly[ClassTag[A1]] == implicitly[ClassTag[B1]]) Option(head match { case x: B1 @unchecked => x })
    else tail.find[B1](n)
  override def fieldNames: List[String] = name :: tail.fieldNames
  override def canEqual(that: Any): Boolean = that.isInstanceOf[LCons[_, _]]
  override def productArity: Int = 3
  override def productElement(n: Int): Any = n match {
    case 0 => name
    case 1 => head
    case 2 => tail
    case _ => throw new IndexOutOfBoundsException(Integer.toString(n))
  }
  def copy[B1: JsonFormat: ClassTag, B2 <: LList: JsonFormat](
      name: String = name,
      head: B1 = head,
      tail: B2 = tail): LCons[B1, B2] =
    new LCons[B1, B2](name, head, tail)
  override def hashCode(): Int = scala.runtime.ScalaRunTime._hashCode(this)
  override def equals(x: Any): Boolean = x match {
    case that: LCons[_, _] =>
      (this.name == that.name) && (this.head == that.head) && (this.tail == that.tail)
    case _ =>
      false
  }
}

object LCons {
  override def toString: String = "LCons"
  def apply[A1: JsonFormat: ClassTag, A2 <: LList: JsonFormat](
      name: String,
      head: A1,
      tail: A2): LCons[A1, A2] =
    new LCons[A1, A2](name = name, head = head, tail = tail)
  def unapply[A1, A2 <: LList](x: LCons[A1, A2]): Option[(String, A1, A2)] =
    if (x == null) None
    else Some((x.name, x.head, x.tail))
}

trait LListFormats {
  import BasicJsonProtocol._

  implicit val lnilFormat: JsonFormat[LNil] = new JsonFormat[LNil] {
    def write[J](x: LNil, builder: Builder[J]): Unit = {
      if (!builder.isInObject) builder.beginObject()
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): LNil = {
      if (unbuilder.isInObject) unbuilder.endObject()
      LNil
    }
  }

  private val fieldNamesField = "$fields"

  implicit def lconsFormat[A1: JsonFormat: ClassTag, A2 <: LList: JsonFormat]: JsonFormat[LCons[A1, A2]] =
    new JsonFormat[LCons[A1, A2]] {
      val a1Format: JsonFormat[A1] = implicitly
      val a2Format: JsonFormat[A2] = implicitly

      def write[J](x: LCons[A1, A2], builder: Builder[J]): Unit = {
        if (!builder.isInObject) {
          builder.beginPreObject()
          builder.addField(fieldNamesField, x.fieldNames)
          builder.endPreObject()
          builder.beginObject()
        }
        builder.addField(x.name, x.head)(a1Format)
        a2Format.write(x.tail, builder)
      }

      def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): LCons[A1, A2] =
        jsOpt match {
          case Some(js) =>
            def objectPreamble(x: J) = {
              unbuilder.beginPreObject(x)
              val jf = implicitly[JsonFormat[Vector[String]]]
              val fieldNames = unbuilder.lookupField(fieldNamesField).map(x => jf.read(Some(x), unbuilder))
              unbuilder.endPreObject()
              unbuilder.beginObject(x, fieldNames)
            }
            if (!unbuilder.isInObject) objectPreamble(js)
            if (unbuilder.hasNextField) {
              val (name, optX) = unbuilder.nextFieldOpt()
              optX foreach { x =>
                if (unbuilder.isObject(x)) objectPreamble(x)
              }
              val elem = a1Format.read(optX, unbuilder)
              val tail = a2Format.read(Option(js), unbuilder)
              LCons(name, elem, tail)
            }
            else deserializationError(s"Unexpected end of object: $js")
          case None =>
            val elem = a1Format.read(None, unbuilder)
            val tail = a2Format.read(None, unbuilder)
            LCons("*", elem, tail)
        }
    }
}

final class LListOps[L <: LList](l: L) {
  import Nat._
  def _1(implicit at: At[L, _0]): at.Out = at(l)
  def _2(implicit at: At[L, _1]): at.Out = at(l)
  def _3(implicit at: At[L, _2]): at.Out = at(l)
  def _4(implicit at: At[L, _3]): at.Out = at(l)
  def _5(implicit at: At[L, _4]): at.Out = at(l)
  def _6(implicit at: At[L, _5]): at.Out = at(l)
  def _7(implicit at: At[L, _6]): at.Out = at(l)
  def _8(implicit at: At[L, _7]): at.Out = at(l)
  def _9(implicit at: At[L, _8]): at.Out = at(l)
  def _10(implicit at: At[L, _9]): at.Out = at(l)
  def _11(implicit at: At[L, _10]): at.Out = at(l)
  def _12(implicit at: At[L, _11]): at.Out = at(l)
  def _13(implicit at: At[L, _12]): at.Out = at(l)
  def _14(implicit at: At[L, _13]): at.Out = at(l)
  def _15(implicit at: At[L, _14]): at.Out = at(l)
  def _16(implicit at: At[L, _15]): at.Out = at(l)
  def _17(implicit at: At[L, _16]): at.Out = at(l)
  def _18(implicit at: At[L, _17]): at.Out = at(l)
  def _19(implicit at: At[L, _18]): at.Out = at(l)
  def _20(implicit at: At[L, _19]): at.Out = at(l)
  def _21(implicit at: At[L, _20]): at.Out = at(l)
  def _22(implicit at: At[L, _21]): at.Out = at(l)
}

trait At[L <: LList, N <: Nat] {
  type Out
  def apply(t: L): Out
}

object At {
  import Nat._
  type Aux[L <: LList, N <: Nat, Out0] = At[L, N] { type Out = Out0 }

  implicit def at0[H, T <: LList]: Aux[H :*: T, _0, H] =
    new At[H :*: T, _0] {
      type Out = H
      def apply(l: H :*: T): Out = l.head
    }

  implicit def atN[H, T <: LList, N <: Nat, AtOut]
      (implicit att: Aux[T, N, AtOut]): Aux[H :*: T, Succ[N], AtOut] =
    new At[H :*: T, Succ[N]] {
      type Out = AtOut
      def apply(l: H :*: T): Out = att(l.tail)
    }
}

trait Nat { type N <: Nat }
object Nat {
  final class _0 extends Nat { type N = _0 }
  final case class Succ[P <: Nat]() extends Nat { type N = Succ[P] }

  type _1 = Succ[_0]; type _2 = Succ[_1]; type _3 = Succ[_2]; type _4 = Succ[_3]
  type _5 = Succ[_4]; type _6 = Succ[_5]; type _7 = Succ[_6]; type _8 = Succ[_7]
  type _9 = Succ[_8]; type _10 = Succ[_9]; type _11 = Succ[_10]; type _12 = Succ[_11]
  type _13 = Succ[_12]; type _14 = Succ[_13]; type _15 = Succ[_14]; type _16 = Succ[_15]
  type _17 = Succ[_16]; type _18 = Succ[_17]; type _19 = Succ[_18]; type _20 = Succ[_19]
  type _21 = Succ[_20]; type _22 = Succ[_21]
}
