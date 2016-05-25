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

import org.specs2.mutable._
import java.util.Arrays
import spray.json.{ JsArray, JsNumber, JsString, JsObject }
import LList._

class BuilderSpec extends Specification with BasicJsonProtocol {
  case class Person(name: String, value: Int)
  implicit object PersonFormat extends JsonFormat[Person] {
    def write[J](x: Person, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("name")
      builder.writeString(x.name)
      builder.addField("value")
      builder.writeInt(x.value)
      builder.endObject()
    }
    def read[J](js: J, unbuilder: Unbuilder[J]): Person = {
      unbuilder.beginObject(js)
      val name = unbuilder.lookupField("name") match {
        case Some(x) => unbuilder.readString(x)
        case _       => deserializationError(s"Missing field: name")
      }
      val value = unbuilder.lookupField("value") match {
        case Some(x) => unbuilder.readInt(x)
        case _       => 0
      }
      unbuilder.endObject()
      Person(name, value)
    }
  }

  "Custom format using builder" should {
    val p1 = Person("Alice", 1)
    val personJs = JsObject("name" -> JsString("Alice"), "value" -> JsNumber(1))
    "convert from value to JObject" in {
      Converter.toJsonUnsafe(p1) mustEqual personJs
    }
    "convert from JObject to the same value" in {
      Converter.fromJsonUnsafe[Person](personJs) mustEqual p1
    }
  }
}
