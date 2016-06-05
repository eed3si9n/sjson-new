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
import org.specs2.mutable._
import scala.Right
import java.util.UUID
import java.net.{ URI, URL }

class StandardFormatsSpec extends Specification with BasicJsonProtocol {
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

  "The optionFormat" should {
    "convert None to JsNull" in {
      Converter.toJsonUnsafe(None.asInstanceOf[Option[Int]]) mustEqual JsNull
    }
    "convert JsNull to None" in {
      Converter.fromJsonUnsafe[Option[Int]](JsNull) mustEqual None
    }
    "convert Some(Hello) to JsString(Hello)" in {
      Converter.toJsonUnsafe(Some("Hello").asInstanceOf[Option[String]]) mustEqual JsString("Hello")
    }
    "convert JsString(Hello) to Some(Hello)" in {
      Converter.fromJsonUnsafe[Option[String]](JsString("Hello")) mustEqual Some("Hello")
    }
    "omit None fields" in {
      Converter.toJsonUnsafe(Person(None, None)) mustEqual JsObject()
    }
  }

  "The eitherFormat" should {
    val a: Either[Int, String] = Left(42)
    val b: Either[Int, String] = Right("Hello")

    "convert the left side of an Either value to Json" in {
      Converter.toJsonUnsafe(a) mustEqual JsNumber(42)
    }
    "convert the right side of an Either value to Json" in {
      Converter.toJsonUnsafe(b) mustEqual JsString("Hello")
    }
    "convert the left side of an Either value from Json" in {
      Converter.fromJsonUnsafe[Either[Int, String]](JsNumber(42)) mustEqual Left(42)
    }
    "convert the right side of an Either value from Json" in {
      Converter.fromJsonUnsafe[Either[Int, String]](JsString("Hello")) mustEqual Right("Hello")
    }
  }

  "The uuidStringIso" should {
    val uuid = UUID.fromString("abc220ea-2a01-11e6-b67b-9e71128cae77")
    "convert a UUID to JsString" in {
      Converter.toJsonUnsafe(uuid) mustEqual JsString("abc220ea-2a01-11e6-b67b-9e71128cae77")
    }
    "convert the JsString back to the UUID" in {
      Converter.fromJsonUnsafe[UUID](JsString("abc220ea-2a01-11e6-b67b-9e71128cae77")) mustEqual uuid
    }
  }

  "The uriStringIso" should {
    val uri = new URI("http://localhost")
    "convert a URI to JsString" in {
      Converter.toJsonUnsafe(uri) mustEqual JsString("http://localhost")
    }
    "convert the JsString back to the URI" in {
      Converter.fromJsonUnsafe[URI](JsString("http://localhost")) mustEqual uri
    }
  }

  "The urlStringIso" should {
    val url = new URL("http://localhost")
    "convert a URL to JsString" in {
      Converter.toJsonUnsafe(url) mustEqual JsString("http://localhost")
    }
    "convert the JsString back to the URI" in {
      Converter.fromJsonUnsafe[URL](JsString("http://localhost")) mustEqual url
    }
  }
}
