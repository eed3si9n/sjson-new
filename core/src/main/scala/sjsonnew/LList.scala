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

/** Heterogeneous list with labels. */
sealed trait LList {
  // type Wrap[F[_]] <: LList
  def find[A1: ClassManifest](n: String): Option[A1]
}
sealed trait LNil extends LList {
  import LList.:*:
  // type Wrap[F[_]] = LNil
  def :*:[A1: JsonFormat: ClassManifest](labelled: (String, A1)): A1 :*: LNil = LCons(labelled._1, labelled._2, this)

  override def toString: String = "LNil"
  override def find[A1: ClassManifest](n: String): Option[A1] = None
}
object LNil extends LNil {
  implicit val singletonFormat: JsonFormat[LNil.type] = forLNil[LNil.type](LNil)
  implicit val lnilFormat: JsonFormat[LNil] = forLNil[LNil](LNil)

  private def forLNil[A <: LNil](lnil: A): JsonFormat[A] = new JsonFormat[A] {
    def write[J](x: A, builder: Builder[J]): Unit =
      {
        if (!builder.isInObject) {
          builder.beginObject()
        }
        builder.endObject()
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): A =
      {
        if (unbuilder.isInObject) {
          unbuilder.endObject()
        }
        lnil
      }
  }
}

final case class LCons[A1: JsonFormat: ClassManifest, A2 <: LList: JsonFormat](name: String, head: A1, tail: A2) extends LList {
  import LList.:*:
  // type Wrap[F[_]] = F[A1] :*: A2#Wrap[F]
  def :*:[B1: JsonFormat: ClassManifest](labelled: (String, B1)): B1 :*: A1 :*: A2 = LCons(labelled._1, labelled._2, this)
  override def toString: String = s"($name, $head) :*: $tail"
  override def find[B1: ClassManifest](n: String): Option[B1] =
    if (name == n && implicitly[ClassManifest[A1]] == implicitly[ClassManifest[B1]]) Option(head match { case x: B1 @unchecked => x })
    else tail.find[B1](n)
}
object LCons {
  implicit def lconsFormat[A1: JsonFormat: ClassManifest, A2 <: LList: JsonFormat]: JsonFormat[LCons[A1, A2]] = new JsonFormat[LCons[A1, A2]] {
    val a1Format = implicitly[JsonFormat[A1]]
    val a2Format = implicitly[JsonFormat[A2]]
    def write[J](x: LCons[A1, A2], builder: Builder[J]): Unit =
      {
        if (!builder.isInObject) {
          builder.beginObject()
        }
        builder.addField(x.name, x.head)(a1Format)
        a2Format.write(x.tail, builder)
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): LCons[A1, A2] =
      jsOpt match {
        case Some(js) =>
          if (!unbuilder.isInObject) {
            unbuilder.beginObject(js)
          }
          if (unbuilder.hasNextField) {
            val (name, x) = unbuilder.nextField
            if (unbuilder.isObject(x)) {
              unbuilder.beginObject(x)
            }
            val elem = a1Format.read(Some(x), unbuilder)
            val tail = a2Format.read(Some(js), unbuilder)
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

object LList {
  type :*:[A1, A2 <: LList] = LCons[A1, A2]
  val :*: = LCons
  def iso[A, R0 <: LList: JsonFormat](to0: A => R0, from0: R0 => A): IsoLList.Aux[A, R0] =
    IsoLList.iso[A, R0](to0, from0)
}
