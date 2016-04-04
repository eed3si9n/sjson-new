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

import support.spray.Converter
import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse, JsArray }
import org.specs2.mutable._
import scala.Right

class TupleFormatsSpec extends Specification with BasicJsonProtocol {

  "The tuple1Format" should {
    "convert (42) to a JsNumber" in {
      Converter.toJsonUnsafe(Tuple1(42)) mustEqual JsArray(JsNumber(42))
    }
    "be able to convert a JsNumber to a Tuple1[Int]" in {
      Converter.fromJsonUnsafe[Tuple1[Int]](JsArray(JsNumber(42))) mustEqual Tuple1(42)
    }
  }

  "The tuple2Format" should {
    val json = JsArray(JsNumber(42), JsNumber(4.2))
    "convert (42, 4.2) to a JsArray" in {
      Converter.toJsonUnsafe((42, 4.2)) mustEqual json
    }
    "be able to convert a JsArray to a (Int, Double)]" in {
      Converter.fromJsonUnsafe[(Int, Double)](json) mustEqual (42, 4.2)
    }
  }

  "The tuple3Format" should {
    val json = JsArray(JsNumber(42), JsNumber(4.2), JsString("hello"))
    "convert (42, 4.2, \"hello\") to a JsArray" in {
      Converter.toJsonUnsafe((42, 4.2, "hello")) mustEqual json
    }
    "be able to convert a JsArray to a (Int, Double, Int)]" in {
      Converter.fromJsonUnsafe[(Int, Double, String)](json) mustEqual (42, 4.2, "hello")
    }
  }

}
