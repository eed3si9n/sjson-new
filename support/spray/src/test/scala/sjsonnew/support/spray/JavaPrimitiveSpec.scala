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
import java.lang.{ Integer => JInteger, Long => JLong, Boolean => JBoolean,
  Float => JFloat, Double => JDouble, Byte => JByte, Short => JShort,
  Character => JCharacter }

object JavaPrimitiveFormatsSpec extends verify.BasicTestSuite {
  test("The JIntegerJsonFormat") {
    // "convert an JInteger to a JsNumber" in {
    Predef.assert(Converter.toJsonUnsafe[JInteger](42: JInteger) == JsNumber(42))

    // "convert a JsNumber to an Int" in {
    Predef.assert(Converter.fromJsonUnsafe[JInteger](JsNumber(42)) == (42: JInteger))
  }

  test("The JLongJsonFormat") {
    // "convert a JLong to a JsNumber" in {
    Predef.assert(Converter.toJsonUnsafe[JLong](7563661897011259335L: JLong) == JsNumber(7563661897011259335L))

    // "convert a JsNumber to a JLong" in {
    Predef.assert(Converter.fromJsonUnsafe[JLong](JsNumber(7563661897011259335L)) == (7563661897011259335L: JLong))
  }

  // TODO: Builder doesn't capture Float
  // "The JFloatJsonFormat" should {
  //   "convert a JFloat to a JsNumber" in {
  //     Predef.assert(Converter.toJsonUnsafe[JFloat](4.2f: JFloat) == JsNumber(4.2f))
  //   }
  //   "convert a JsNumber to a JFloat" in {
  //     Predef.assert(Converter.fromJsonUnsafe[JFloat](JsNumber(4.2f)) == (4.2f: JFloat))
  //   }
  // }

  test("The JDoubleJsonFormat") {
    // "convert a JDouble to a JsNumber" in {
    Predef.assert(Converter.toJsonUnsafe[JDouble](4.2: JDouble) == JsNumber(4.2))

    // "convert a JsNumber to a JDouble" in {
    Predef.assert(Converter.fromJsonUnsafe[JDouble](JsNumber(4.2)) == (4.2: JDouble))
  }

  test("The JByteJsonFormat") {
    // "convert a JByte to a JsNumber" in {
    Predef.assert(Converter.toJsonUnsafe[JByte](42.toByte: JByte) == JsNumber(42))

    // "convert a JsNumber to a JByte" in {
    Predef.assert(Converter.fromJsonUnsafe[JByte](JsNumber(42)) == (42.toByte: JByte))
  }

  test("The JShortJsonFormat") {
    // "convert a JShort to a JsNumber" in {
    Predef.assert(Converter.toJsonUnsafe(42.toShort: JShort) == JsNumber(42))

    // "convert a JsNumber to a JShort" in {
    Predef.assert(Converter.fromJsonUnsafe[JShort](JsNumber(42)) == (42.toShort: JShort))
  }

  test("The JBooleanJsonFormat") {
    // "convert true to a JsTrue" in {
    Predef.assert(Converter.toJsonUnsafe[JBoolean](true: JBoolean) == JsTrue)

    // "convert false to a JsFalse" in {
    Predef.assert(Converter.toJsonUnsafe[JBoolean](false: JBoolean) == JsFalse)

    // "convert a JsTrue to true" in {
    Predef.assert(Converter.fromJsonUnsafe[JBoolean](JsTrue) == true)

    // "convert a JsFalse to false" in {
    Predef.assert(Converter.fromJsonUnsafe[JBoolean](JsFalse) == false)
  }

  test("The JCharacterJsonFormat") {
    // "convert a JCharacter to a JsString" in {
    Predef.assert(Converter.toJsonUnsafe[JCharacter]('c': JCharacter) == JsString("c"))

    // "convert a JsString to a JCharacter" in {
    Predef.assert(Converter.fromJsonUnsafe[JCharacter](JsString("c")) == ('c': JCharacter))
  }
}
