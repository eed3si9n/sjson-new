/*
 * Copyright (C) 2011 Mathias Doenitz
 * Adapted and extended in 2016 by Eugene Yokota
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

import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse, JsObject }
import scala.Right

object StandardFormatsSpec extends verify.BasicTestSuite {
  case class Person(name: Option[String], value: Option[Int])
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
          val name = unbuilder.readField[Option[String]]("name")
          val value = unbuilder.readField[Option[Int]]("value")
          unbuilder.endObject()
          Person(name, value)
        case None =>
          deserializationError("Expected JsObject but found None")
      }
  }

  test("The optionFormat") {
    // "convert None to JsNull"
    Predef.assert(Converter.toJsonUnsafe(None.asInstanceOf[Option[Int]]) == JsNull)

    // "convert JsNull to None"
    Predef.assert(Converter.fromJsonUnsafe[Option[Int]](JsNull) == None)

    // "convert Some(Hello) to JsString(Hello)"
    Predef.assert(Converter.toJsonUnsafe(Some("Hello").asInstanceOf[Option[String]]) == JsString("Hello"))

    // "convert JsString(Hello) to Some(Hello)"
    Predef.assert(Converter.fromJsonUnsafe[Option[String]](JsString("Hello")) == Some("Hello"))

    // "omit None fields"
    Predef.assert(Converter.toJsonUnsafe(Person(None, None)) == JsObject())
  }

  test("The eitherFormat") {
    val a: Either[Int, String] = Left(42)
    val b: Either[Int, String] = Right("Hello")

    // "convert the left side of an Either value to Json"
    Predef.assert(Converter.toJsonUnsafe(a) == JsNumber(42))

    // "convert the right side of an Either value to Json"
    Predef.assert(Converter.toJsonUnsafe(b) == JsString("Hello"))

    // "convert the left side of an Either value from Json"
    Predef.assert(Converter.fromJsonUnsafe[Either[Int, String]](JsNumber(42)) == Left(42))

    // "convert the right side of an Either value from Json"
    Predef.assert(Converter.fromJsonUnsafe[Either[Int, String]](JsString("Hello")) == Right("Hello"))
  }
}
