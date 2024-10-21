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

import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse, JsObject }
import java.net.{ URI, URL }
import java.io.File
import java.util.{ Locale, Optional, UUID }

object JavaExtraFormatsSpec extends verify.BasicTestSuite with BasicJsonProtocol {
  case class Person(name: Optional[String], value: Optional[Int])
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
          val name = unbuilder.readField[Optional[String]]("name")
          val value = unbuilder.readField[Optional[Int]]("value")
          unbuilder.endObject()
          Person(name, value)
        case None =>
          deserializationError("Expected JsObject but found None")
      }
  }

  test("The uuidStringIso") {
    val uuid = UUID.fromString("abc220ea-2a01-11e6-b67b-9e71128cae77")
    // "convert a UUID to JsString" in {
    Predef.assert(Converter.toJsonUnsafe(uuid) == JsString("abc220ea-2a01-11e6-b67b-9e71128cae77"))

    // "convert the JsString back to the UUID" in {
    Predef.assert(Converter.fromJsonUnsafe[UUID](JsString("abc220ea-2a01-11e6-b67b-9e71128cae77")) == uuid)
  }

  test("The uriStringIso") {
    val uri = new URI("http://localhost")
    // "convert a URI to JsString" in {
    Predef.assert(Converter.toJsonUnsafe(uri) == JsString("http://localhost"))

    // "convert the JsString back to the URI" in {
    Predef.assert(Converter.fromJsonUnsafe[URI](JsString("http://localhost")) == uri)
  }

  test("The urlStringIso") {
    val url = new URI("http://localhost").toURL
    // "convert a URL to JsString" in {
    Predef.assert(Converter.toJsonUnsafe(url) == JsString("http://localhost"))
    // "convert the JsString back to the URI" in {
    Predef.assert(Converter.fromJsonUnsafe[URL](JsString("http://localhost")) == url)
  }

  test("The optionalFormat") {
    // "convert Optional.empty to JsNull" in {
    Predef.assert(Converter.toJsonUnsafe(Optional.empty[Int]) == JsNull)

    // "convert JsNull to None" in {
    Predef.assert(Converter.fromJsonUnsafe[Optional[Int]](JsNull) == Optional.empty[Int])

    // "convert Some(Hello) to JsString(Hello)" in {
    Predef.assert(Converter.toJsonUnsafe(Optional.of("Hello")) == JsString("Hello"))

    // "convert JsString(Hello) to Some(Hello)" in {
    Predef.assert(Converter.fromJsonUnsafe[Optional[String]](JsString("Hello")) == Optional.of("Hello"))

    // "omit None fields" in {
    Predef.assert(Converter.toJsonUnsafe(Person(Optional.empty[String], Optional.empty[Int])) == JsObject())

  }
}
