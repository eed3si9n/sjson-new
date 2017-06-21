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

import spray.json.{JsonFormat => _, _}
import org.specs2.mutable._

class CaseClassFormatsSpec extends Specification with BasicJsonProtocol {

  private case class Foo(a: Int, b: String)
  private val foo = Foo(42, "bar")

  "case class Json Object Format" should {
    implicit val instance: JsonFormat[Foo] =
      BasicJsonProtocol.caseClass(Foo.apply _, Foo.unapply _)("a", "b")

    val json = JsObject("a" -> JsNumber(42), "b" -> JsString("bar"))

    "convert to a JsObject" in {
      Converter.toJsonUnsafe(foo) mustEqual json
    }
    "be able to convert a JsObject to a case class" in {
      Converter.fromJsonUnsafe[Foo](json) mustEqual foo
    }
  }

  "case class Json Array Format" should {
    implicit val instance: JsonFormat[Foo] =
      BasicJsonProtocol.caseClassArray(Foo.apply _, Foo.unapply _)

    val json = JsArray(JsNumber(42), JsString("bar"))

    "convert to a JsArray" in {
      Converter.toJsonUnsafe(foo) mustEqual json
    }
    "be able to convert a JsArray to a case class" in {
      Converter.fromJsonUnsafe[Foo](json) mustEqual foo
    }
  }

}
