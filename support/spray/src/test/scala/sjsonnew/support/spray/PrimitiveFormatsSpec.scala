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

object PritimiveFormatsSpec extends verify.BasicTestSuite {

  test("The IntJsonFormat") {
    // "convert an Int to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe[Int](42) == JsNumber(42))

    // "convert a JsNumber to an Int"
    Predef.assert(Converter.fromJsonUnsafe[Int](JsNumber(42)) == 42)
  }

  test("The LongJsonFormat") {
    // "convert a Long to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe[Long](7563661897011259335L) == JsNumber(7563661897011259335L))

    // "convert a JsNumber to a Long"
    Predef.assert(Converter.fromJsonUnsafe[Long](JsNumber(7563661897011259335L)) == 7563661897011259335L)
  }

  test("The FloatJsonFormat") {
    // "convert a Float to a JsNumber"
    // Predef.assert(Converter.toJsonUnsafe(4.2f) == JsNumber(4.2f))

    // "convert a Float.NaN to a JsNull"
    Predef.assert(Converter.toJsonUnsafe(Float.NaN) == JsNull)

    // "convert a Float.PositiveInfinity to a JsNull"
    Predef.assert(Converter.toJsonUnsafe(Float.PositiveInfinity) == JsNull)

    // "convert a Float.NegativeInfinity to a JsNull"
    Predef.assert(Converter.toJsonUnsafe(Float.NegativeInfinity) == JsNull)

    // "convert a JsNumber to a Float"
    Predef.assert(Converter.fromJsonUnsafe[Float](JsNumber(4.2f)) == 4.2f)

    // "convert a JsNull to a Float"
    Predef.assert(Converter.fromJsonUnsafe[Float](JsNull).isNaN == Float.NaN.isNaN)
  }

  test("The DoubleJsonFormat") {
    // "convert a Double to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe(4.2) == JsNumber(4.2))

    // "convert a Double.NaN to a JsNull"
    Predef.assert(Converter.toJsonUnsafe(Double.NaN) == JsNull)

    // "convert a Double.PositiveInfinity to a JsNull"
    Predef.assert(Converter.toJsonUnsafe(Double.PositiveInfinity) == JsNull)

    // "convert a Double.NegativeInfinity to a JsNull"
    Predef.assert(Converter.toJsonUnsafe(Double.NegativeInfinity) == JsNull)

    // "convert a JsNumber to a Double"
    Predef.assert(Converter.fromJsonUnsafe[Double](JsNumber(4.2)) == 4.2)

    // "convert a JsNull to a Double"
    Predef.assert(Converter.fromJsonUnsafe[Double](JsNull).isNaN == Double.NaN.isNaN)
  }

  test("The ByteJsonFormat") {
    // "convert a Byte to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe(42.asInstanceOf[Byte]) == JsNumber(42))

    // "convert a JsNumber to a Byte"
    Predef.assert(Converter.fromJsonUnsafe[Byte](JsNumber(42)) == 42)
  }

  test("The ShortJsonFormat") {
    // "convert a Short to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe(42.asInstanceOf[Short]) == JsNumber(42))

    // "convert a JsNumber to a Short"
    Predef.assert(Converter.fromJsonUnsafe[Short](JsNumber(42)) == 42)
  }

  test("The BigDecimalJsonFormat") {
    // "convert a BigDecimal to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe(BigDecimal(42)) == JsNumber(42))

    // "convert a JsNumber to a BigDecimal"
    Predef.assert(Converter.fromJsonUnsafe[BigDecimal](JsNumber(42)) == BigDecimal(42))
  }

  test("The BigIntJsonFormat") {
    // "convert a BigInt to a JsNumber"
    Predef.assert(Converter.toJsonUnsafe(BigInt(42)) == JsNumber(42))

    // "convert a JsNumber to a BigInt"
    Predef.assert(Converter.fromJsonUnsafe[BigDecimal](JsNumber(42)) == BigInt(42))
  }

  test("The UnitJsonFormat") {
    // "convert Unit to a JsNumber(1)"
    Predef.assert(Converter.toJsonUnsafe(()) == JsNumber(1))

    // "convert a JsNumber to Unit"
    Predef.assert(Converter.fromJsonUnsafe[Unit](JsNumber(1)) == (()))
  }

  test("The BooleanJsonFormat") {
    // "convert true to a JsTrue"
    Predef.assert(Converter.toJsonUnsafe(true) == JsTrue)

    // "convert false to a JsFalse"
    Predef.assert(Converter.toJsonUnsafe(false) == JsFalse)

    // "convert a JsTrue to true"
    Predef.assert(Converter.fromJsonUnsafe[Boolean](JsTrue) == true)

    // "convert a JsFalse to false"
    Predef.assert(Converter.fromJsonUnsafe[Boolean](JsFalse) == false)
  }

  test("The CharJsonFormat") {
    // "convert a Char to a JsString"
    Predef.assert(Converter.toJsonUnsafe('c') == JsString("c"))

    // "convert a JsString to a Char"
    Predef.assert(Converter.fromJsonUnsafe[Char](JsString("c")) == 'c')
  }

  test("The StringJsonFormat") {
    // "convert a String to a JsString"
    Predef.assert(Converter.toJsonUnsafe("Hello") == JsString("Hello"))

    // "convert a JsString to a String"
    Predef.assert(Converter.fromJsonUnsafe[String](JsString("Hello")) == "Hello")
  }

  test("The SymbolJsonFormat") {
    // "convert a Symbol to a JsString"
    Predef.assert(Converter.toJsonUnsafe(Symbol("Hello")) == JsString("Hello"))

    // "convert a JsString to a Symbol"
    Predef.assert(Converter.fromJsonUnsafe[Symbol](JsString("Hello")) == Symbol("Hello"))
  }
}
