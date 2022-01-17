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
package support.spray

import java.util.Arrays
import spray.json.{ JsArray, JsNumber, JsString, JsObject }
import LList._

object BuilderSpec extends BasicJsonProtocol with verify.BasicTestSuite {
  case class Person(name: String, value: Int)
  implicit object PersonFormat extends JsonFormat[Person] {
    def write[J](x: Person, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("name", x.name)
      builder.addField("value", x.value)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Person =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val name = unbuilder.readField[String]("name")
          val value = unbuilder.readField[Int]("value")
          unbuilder.endObject()
          Person(name, value)
        case None =>
          deserializationError("Expected JsObject but found None")
      }
  }

  val p1 = Person("Alice", 1)
  val personJs = JsObject("name" -> JsString("Alice"), "value" -> JsNumber(1))

  test("Custom format using builder should convert from value to JObject") {
    Predef.assert(Converter.toJsonUnsafe(p1) == personJs)
  }

  test("Custom format using builder should convert from JObject to the same value") {
    Predef.assert(Converter.fromJsonUnsafe[Person](personJs) == p1)
  }
}
