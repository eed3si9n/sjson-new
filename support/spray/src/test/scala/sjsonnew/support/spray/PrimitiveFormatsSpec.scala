/*
 * Copyright (C) 2011-2016 Mathias Doenitz
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

import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse }
import org.specs2.mutable._

class PritimiveFormatsSpec extends Specification with BasicJsonProtocol {

  "The IntJsonFormat" should {
    "convert an Int to a JsNumber" in {
      Converter.toJsonUnsafe[Int](42) mustEqual JsNumber(42)
    }
    "convert a JsNumber to an Int" in {
      Converter.fromJsonUnsafe[Int](JsNumber(42)) mustEqual 42
    }
  }

  "The LongJsonFormat" should {
    "convert a Long to a JsNumber" in {
      Converter.toJsonUnsafe[Long](7563661897011259335L) mustEqual JsNumber(7563661897011259335L)
    }
    "convert a JsNumber to a Long" in {
      Converter.fromJsonUnsafe[Long](JsNumber(7563661897011259335L)) mustEqual 7563661897011259335L
    }
  }

  "The FloatJsonFormat" should {
    "convert a Float to a JsNumber" in {
      Converter.toJsonUnsafe(4.2f) mustEqual JsNumber(4.2f)
    }
    "convert a Float.NaN to a JsNull" in {
      Converter.toJsonUnsafe(Float.NaN) mustEqual JsNull
    }
    "convert a Float.PositiveInfinity to a JsNull" in {
      Converter.toJsonUnsafe(Float.PositiveInfinity) mustEqual JsNull
    }
    "convert a Float.NegativeInfinity to a JsNull" in {
      Converter.toJsonUnsafe(Float.NegativeInfinity) mustEqual JsNull
    }
    "convert a JsNumber to a Float" in {
      Converter.fromJsonUnsafe[Float](JsNumber(4.2f)) mustEqual 4.2f
    }
    "convert a JsNull to a Float" in {
      Converter.fromJsonUnsafe[Float](JsNull).isNaN mustEqual Float.NaN.isNaN
    }
  }

  "The DoubleJsonFormat" should {
    "convert a Double to a JsNumber" in {
      Converter.toJsonUnsafe(4.2) mustEqual JsNumber(4.2)
    }
    "convert a Double.NaN to a JsNull" in {
      Converter.toJsonUnsafe(Double.NaN) mustEqual JsNull
    }
    "convert a Double.PositiveInfinity to a JsNull" in {
      Converter.toJsonUnsafe(Double.PositiveInfinity) mustEqual JsNull
    }
    "convert a Double.NegativeInfinity to a JsNull" in {
      Converter.toJsonUnsafe(Double.NegativeInfinity) mustEqual JsNull
    }
    "convert a JsNumber to a Double" in {
      Converter.fromJsonUnsafe[Double](JsNumber(4.2)) mustEqual 4.2
    }
    "convert a JsNull to a Double" in {
      Converter.fromJsonUnsafe[Double](JsNull).isNaN mustEqual Double.NaN.isNaN
    }
  }

  "The ByteJsonFormat" should {
    "convert a Byte to a JsNumber" in {
      Converter.toJsonUnsafe(42.asInstanceOf[Byte]) mustEqual JsNumber(42)
    }
    "convert a JsNumber to a Byte" in {
      Converter.fromJsonUnsafe[Byte](JsNumber(42)) mustEqual 42
    }
  }

  "The ShortJsonFormat" should {
    "convert a Short to a JsNumber" in {
      Converter.toJsonUnsafe(42.asInstanceOf[Short]) mustEqual JsNumber(42)
    }
    "convert a JsNumber to a Short" in {
      Converter.fromJsonUnsafe[Short](JsNumber(42)) mustEqual 42
    }
  }

  "The BigDecimalJsonFormat" should {
    "convert a BigDecimal to a JsNumber" in {
      Converter.toJsonUnsafe(BigDecimal(42)) mustEqual JsNumber(42)
    }
    "convert a JsNumber to a BigDecimal" in {
      Converter.fromJsonUnsafe[BigDecimal](JsNumber(42)) mustEqual BigDecimal(42)
    }
  }

  "The BigIntJsonFormat" should {
    "convert a BigInt to a JsNumber" in {
      Converter.toJsonUnsafe(BigInt(42)) mustEqual JsNumber(42)
    }
    "convert a JsNumber to a BigInt" in {
      Converter.fromJsonUnsafe[BigDecimal](JsNumber(42)) mustEqual BigInt(42)
    }
  }

  "The UnitJsonFormat" should {
    "convert Unit to a JsNumber(1)" in {
      Converter.toJsonUnsafe(()) mustEqual JsNumber(1)
    }
    "convert a JsNumber to Unit" in {
      Converter.fromJsonUnsafe[Unit](JsNumber(1)) mustEqual (())
    }
  }

  "The BooleanJsonFormat" should {
    "convert true to a JsTrue" in { Converter.toJsonUnsafe(true) mustEqual JsTrue }
    "convert false to a JsFalse" in { Converter.toJsonUnsafe(false) mustEqual JsFalse }
    "convert a JsTrue to true" in { Converter.fromJsonUnsafe[Boolean](JsTrue) mustEqual true }
    "convert a JsFalse to false" in { Converter.fromJsonUnsafe[Boolean](JsFalse) mustEqual false }
  }

  "The CharJsonFormat" should {
    "convert a Char to a JsString" in {
      Converter.toJsonUnsafe('c') mustEqual JsString("c")
    }
    "convert a JsString to a Char" in {
      Converter.fromJsonUnsafe[Char](JsString("c")) mustEqual 'c'
    }
  }

  "The StringJsonFormat" should {
    "convert a String to a JsString" in {
      Converter.toJsonUnsafe("Hello") mustEqual JsString("Hello")
    }
    "convert a JsString to a String" in {
      Converter.fromJsonUnsafe[String](JsString("Hello")) mustEqual "Hello"
    }
  }

  "The SymbolJsonFormat" should {
    "convert a Symbol to a JsString" in {
      Converter.toJsonUnsafe('Hello) mustEqual JsString("Hello")
    }
    "convert a JsString to a Symbol" in {
      Converter.fromJsonUnsafe[Symbol](JsString("Hello")) mustEqual 'Hello
    }
  }

}
