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

import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse, JsArray }
import scala.Right

object TupleFormatsSpec extends verify.BasicTestSuite with BasicJsonProtocol {

  test("The tuple1Format") {
    // "convert (42) to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe(Tuple1(42)) == JsArray(JsNumber(42)))

    // "be able to convert a JsNumber to a Tuple1[Int]"
    Predef.assert(Converter.fromJsonUnsafe[Tuple1[Int]](JsArray(JsNumber(42))) == Tuple1(42))
  }

  test("The tuple2Format") {
    val json = JsArray(JsNumber(42), JsNumber(4.2))
    // "convert (42, 4.2) to a JsArray"
    Predef.assert(Converter.toJsonUnsafe((42, 4.2)) == json)

    // "be able to convert a JsArray to a (Int, Double)]"
    Predef.assert(Converter.fromJsonUnsafe[(Int, Double)](json) == (42, 4.2))
  }

  test("The tuple3Format") {
    val json = JsArray(JsNumber(42), JsNumber(4.2), JsString("hello"))
    // "convert (42, 4.2, \"hello\") to a JsArray"
    Predef.assert(Converter.toJsonUnsafe((42, 4.2, "hello")) == json)

    // "be able to convert a JsArray to a (Int, Double, Int)]"
    Predef.assert(Converter.fromJsonUnsafe[(Int, Double, String)](json) == (42, 4.2, "hello"))
  }
}
