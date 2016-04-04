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
import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse }
import org.specs2.mutable._
import scala.Right

class StandardFormatsSpec extends Specification with BasicJsonProtocol {

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
}
