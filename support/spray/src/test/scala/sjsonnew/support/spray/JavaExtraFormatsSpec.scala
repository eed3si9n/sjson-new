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
import org.specs2.mutable._
import java.net.{ URI, URL }
import java.io.File
import java.util.{ Locale, Optional, UUID }

class JavaExtraFormatsSpec extends Specification with BasicJsonProtocol {
  import JavaExtraFormats._

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

  "The fileStringIso" should {
    val f = new File("/tmp")
    val f2 = new File(new File("src"), "main")
    "convert a File to JsString" in {
      Converter.toJsonUnsafe(f) mustEqual JsString("file:///tmp")
    }
    "convert a relative path to JsString" in {
      // https://tools.ietf.org/html/rfc3986#section-4.2
      Converter.toJsonUnsafe(f2) mustEqual JsString("src/main")
    }
    "convert the JsString back to the File" in {
      Converter.fromJsonUnsafe[File](JsString("file:///tmp")) mustEqual f
    }
    "convert the JsString back to the relative path" in {
      Converter.fromJsonUnsafe[File](JsString("src/main")) mustEqual f2
    }
    "convert an absolute path on Windows" in {
      if (isWindows) Converter.toJsonUnsafe(new File("""C:\Documents and Settings\""")) mustEqual JsString("file:///C:/Documents%20and%20Settings")
      else ok
    }
    "convert a relative path on Windows" in {
      if (isWindows) Converter.toJsonUnsafe(new File("""..\My Documents\test""")) mustEqual JsString("../My%20Documents/test")
      else ok
    }
    "convert a UNC path on Windows" in {
      if (isWindows) Converter.toJsonUnsafe(new File("""\\laptop\My Documents\Some.doc""")) mustEqual JsString("file://laptop/My%20Documents/Some.doc")
      else ok
    }
  }

  "The optionalFormat" should {
    "convert Optional.empty to JsNull" in {
      Converter.toJsonUnsafe(Optional.empty[Int]) mustEqual JsNull
    }
    "convert JsNull to None" in {
      Converter.fromJsonUnsafe[Optional[Int]](JsNull) mustEqual Optional.empty[Int]
    }
    "convert Some(Hello) to JsString(Hello)" in {
      Converter.toJsonUnsafe(Optional.of("Hello")) mustEqual JsString("Hello")
    }
    "convert JsString(Hello) to Some(Hello)" in {
      Converter.fromJsonUnsafe[Optional[String]](JsString("Hello")) mustEqual Optional.of("Hello")
    }
    "omit None fields" in {
      Converter.toJsonUnsafe(Person(Optional.empty[String], Optional.empty[Int])) mustEqual JsObject()
    }
  }
}
