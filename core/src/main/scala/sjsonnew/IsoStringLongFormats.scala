/*
 * Copyright (C) 2023 Eugene Yokota
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

trait IsoStringLongFormats {
  implicit def isoStringLongFormat[A: IsoStringLong]: JsonFormat[A] = new JsonFormat[A] {
    val iso = implicitly[IsoStringLong[A]]
    def write[J](a: A, builder: Builder[J]): Unit = {
      val p = iso.to(a)
      builder.beginObject()
      builder.addFieldName("first")
      builder.writeString(p._1)
      builder.addFieldName("second")
      builder.writeLong(p._2)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): A =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val first = unbuilder.readField[String]("first")
          val second = unbuilder.readField[Long]("second")
          unbuilder.endObject()
          iso.from((first, second))
        case None => deserializationError(s"Expected JsObject but got None")
      }
  }
}
