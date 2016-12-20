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

class LListFormatsSpec extends Specification with BasicJsonProtocol {

  "The llistFormat" should {
    val empty = LNil
    val emptyObject = JsObject()
    val list = ("Z", 2) :*: ("a", 1) :*: LNil
    val obj = JsObject("$fields" -> JsArray(JsString("Z"), JsString("a")), "Z" -> JsNumber(2), "a" -> JsNumber(1))
    val nested = ("b", list) :*: LNil
    val nestedObj = JsObject("$fields" -> JsArray(JsString("b")), "b" -> obj)
    "convert an empty list to JObject" in {
      Converter.toJsonUnsafe(empty) mustEqual emptyObject
    }
    "convert a list to JObject" in {
      Converter.toJsonUnsafe(list) mustEqual obj
    }
    "convert a nested list to JObject" in {
      Converter.toJsonUnsafe(nested) mustEqual nestedObj
    }
    "convert a JObject to list" in {
      Converter.fromJsonUnsafe[Int :*: Int :*: LNil](obj) mustEqual list
    }
    "convert a nested JObject to list" in {
      Converter.fromJsonUnsafe[(Int :*: Int :*: LNil) :*: LNil](nestedObj) mustEqual nested
    }

    val obj2 = JsObject("f" -> JsString("foo"))
    val nested2Obj = JsObject("b" -> obj, "c" -> obj2)

    val list2 = ("f", "foo") :*: LNil
    val nested2 = ("b", list) :*: ("c", list2) :*: LNil

    "convert a 2 nested JObjects to list" in {
      Converter.fromJsonUnsafe[(Int :*: LNil) :*: (String :*: LNil) :*: LNil](nested2Obj) mustEqual nested2
    }
  }
}
