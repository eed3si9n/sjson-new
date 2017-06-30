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

/** Heterogeneous list with labels. */
sealed trait LList {
  def find[A1: ClassTag](n: String): Option[A1]
  def fieldNames: List[String]
}

object LList extends LListFormats {
  type :*:[A1, A2 <: LList] = LCons[A1, A2]
  val :*: = LCons
  def iso[A, R0 <: LList: JsonFormat](to0: A => R0, from0: R0 => A): IsoLList.Aux[A, R0] =
    IsoLList.iso[A, R0](to0, from0)
  // This is so the return type of LNil becomes LNil, instead of LNil.type.
  val LNil0: LNil0 = new LNil0 {}
  sealed trait LNil0 extends LList {
    def :*:[A1: JsonFormat: ClassTag](labelled: (String, A1)): A1 :*: LNil = LCons(labelled._1, labelled._2, this)

    override def toString: String = "LNil"
    override def find[A1: ClassTag](n: String): Option[A1] = None
    override def fieldNames: List[String] = Nil
  }
}

final case class LCons[A1: JsonFormat: ClassTag, A2 <: LList: JsonFormat](name: String, head: A1, tail: A2) extends LList {
  import LList.:*:
  def :*:[B1: JsonFormat: ClassTag](labelled: (String, B1)): B1 :*: A1 :*: A2 = LCons(labelled._1, labelled._2, this)
  override def toString: String = s"($name, $head) :*: $tail"
  override def find[B1: ClassTag](n: String): Option[B1] =
    if (name == n && implicitly[ClassTag[A1]] == implicitly[ClassTag[B1]]) Option(head match { case x: B1 @unchecked => x })
    else tail.find[B1](n)
  override def fieldNames: List[String] = name :: tail.fieldNames
}


trait LListFormats {
  import BasicJsonProtocol._

  implicit val lnilFormat: JsonFormat[LNil] = new JsonFormat[LNil] {
    def write[J](x: LNil, builder: Builder[J]): Unit =
      {
        if (!builder.isInObject) {
          builder.beginObject()
        }
        builder.endObject()
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): LNil =
      {
        if (unbuilder.isInObject) {
          unbuilder.endObject()
        }
        LList.LNil0
      }
  }
  private val fieldNamesField = "$fields"
  implicit def lconsFormat[A1: JsonFormat: ClassTag, A2 <: LList: JsonFormat]: JsonFormat[LCons[A1, A2]] = new JsonFormat[LCons[A1, A2]] {
    val a1Format = implicitly[JsonFormat[A1]]
    val a2Format = implicitly[JsonFormat[A2]]
    def write[J](x: LCons[A1, A2], builder: Builder[J]): Unit =
      {
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
            val fieldNames = unbuilder.lookupField(fieldNamesField) match {
              case Some(x) => jf.read(Some(x), unbuilder)
              case None    => deserializationError(s"Field not found: $fieldNamesField")
            }
            unbuilder.endPreObject()
            unbuilder.beginObject(x, Some(fieldNames))
          }
          if (!unbuilder.isInObject) objectPreamble(js)
          if (unbuilder.hasNextField) {
            val (name, x) = unbuilder.nextField
            if (unbuilder.isObject(x)) objectPreamble(x)
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
