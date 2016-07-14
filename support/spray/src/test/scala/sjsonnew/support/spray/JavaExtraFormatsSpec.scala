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
import java.util.UUID
import java.net.{ URI, URL }
import java.io.File

class JavaExtraFormatsSpec extends Specification with BasicJsonProtocol {
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
    "convert a File to JsString" in {
      Converter.toJsonUnsafe(f) mustEqual JsString("file:/tmp/")
    }
    "convert the JsString back to the URI" in {
      Converter.fromJsonUnsafe[File](JsString("file:/tmp/")) mustEqual f
    }
  }
}
