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
}
sealed trait LNil extends LList {
  import LList.:+:
  // type Wrap[F[_]] = LNil
  def :+:[A1: JsonFormat](labelled: (String, A1)): A1 :+: LNil = LCons(labelled._1, labelled._2, this)

  override def toString: String = "LNil"
}
object LNil extends LNil {
  implicit val singletonFormat: JsonFormat[LNil.type] = new JsonFormat[LNil.type] {
    def write[J](x: LNil.type, builder: Builder[J]): Unit =
      {
        if (!builder.isInObject) {
          builder.beginObject()
        }
        builder.endObject()
      }
    def read[J](js: J, unbuilder: Unbuilder[J]): LNil.type = LNil
  }
  implicit val lnilFormat: JsonFormat[LNil] =new JsonFormat[LNil] {
    def write[J](x: LNil, builder: Builder[J]): Unit =
      {
        if (!builder.isInObject) {
          builder.beginObject()
        }
        builder.endObject()
      }
    def read[J](js: J, unbuilder: Unbuilder[J]): LNil.type =
      {
        if (unbuilder.isInObject) {
          unbuilder.endObject()
        }
        LNil
      }
  }
}

final case class LCons[A1: JsonFormat, A2 <: LList: JsonFormat](name: String, head: A1, tail: A2) extends LList {
  import LList.:+:
  // type Wrap[F[_]] = F[A1] :+: A2#Wrap[F]
  def :+:[B1: JsonFormat](labelled: (String, B1)): B1 :+: A1 :+: A2 = LCons(labelled._1, labelled._2, this)
  override def toString: String = head + " :+: " + tail.toString
}
object LCons {
  implicit def lconsFormat[A1: JsonFormat, A2 <: LList: JsonFormat]: JsonFormat[LCons[A1, A2]] = new JsonFormat[LCons[A1, A2]] {
    val a1Format = implicitly[JsonFormat[A1]]
    val a2Format = implicitly[JsonFormat[A2]]
    def write[J](x: LCons[A1, A2], builder: Builder[J]): Unit =
      {
        if (!builder.isInObject) {
          builder.beginObject()
        }
        builder.writeString(x.name)
        a1Format.write(x.head, builder)
        a2Format.write(x.tail, builder)
      }
    def read[J](js: J, unbuilder: Unbuilder[J]): LCons[A1, A2] =
      {
        if (!unbuilder.isInObject) {
          unbuilder.beginObject(js)
        }
        if (unbuilder.hasNextField) {
          val (name, x) = unbuilder.nextField
          if (unbuilder.isObject(x)) {
            unbuilder.beginObject(x)
          }
          val elem = a1Format.read(x, unbuilder)
          val tail = a2Format.read(js, unbuilder)
          LCons(name, elem, tail)
        }
        else deserializationError(s"Unexpected end of object: $js")
      }
  }
}

object LList {
  type :+:[A1, A2 <: LList] = LCons[A1, A2]
  val :+: = LCons
}
