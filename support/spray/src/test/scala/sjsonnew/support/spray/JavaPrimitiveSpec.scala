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
import java.lang.{ Integer => JInteger, Long => JLong, Boolean => JBoolean,
  Float => JFloat, Double => JDouble, Byte => JByte, Short => JShort,
  Character => JCharacter }

class JavaPrimitiveFormatsSpec extends Specification with BasicJsonProtocol {
  "The JIntegerJsonFormat" should {
    "convert an JInteger to a JsNumber" in {
      Converter.toJsonUnsafe[JInteger](42: JInteger) mustEqual JsNumber(42)
    }
    "convert a JsNumber to an Int" in {
      Converter.fromJsonUnsafe[JInteger](JsNumber(42)) mustEqual (42: JInteger)
    }
  }

  "The JLongJsonFormat" should {
    "convert a JLong to a JsNumber" in {
      Converter.toJsonUnsafe[JLong](7563661897011259335L: JLong) mustEqual JsNumber(7563661897011259335L)
    }
    "convert a JsNumber to a JLong" in {
      Converter.fromJsonUnsafe[JLong](JsNumber(7563661897011259335L)) mustEqual (7563661897011259335L: JLong)
    }
  }

  "The JFloatJsonFormat" should {
    "convert a JFloat to a JsNumber" in {
      Converter.toJsonUnsafe[JFloat](4.2f: JFloat) mustEqual JsNumber(4.2f)
    }
    "convert a JsNumber to a JFloat" in {
      Converter.fromJsonUnsafe[JFloat](JsNumber(4.2f)) mustEqual (4.2f: JFloat)
    }
  }

  "The JDoubleJsonFormat" should {
    "convert a JDouble to a JsNumber" in {
      Converter.toJsonUnsafe[JDouble](4.2: JDouble) mustEqual JsNumber(4.2)
    }
    "convert a JsNumber to a JDouble" in {
      Converter.fromJsonUnsafe[JDouble](JsNumber(4.2)) mustEqual (4.2: JDouble)
    }
  }

  "The JByteJsonFormat" should {
    "convert a JByte to a JsNumber" in {
      Converter.toJsonUnsafe[JByte](42.toByte: JByte) mustEqual JsNumber(42)
    }
    "convert a JsNumber to a JByte" in {
      Converter.fromJsonUnsafe[JByte](JsNumber(42)) mustEqual (42.toByte: JByte)
    }
  }

  "The JShortJsonFormat" should {
    "convert a JShort to a JsNumber" in {
      Converter.toJsonUnsafe(42.toShort: JShort) mustEqual JsNumber(42)
    }
    "convert a JsNumber to a JShort" in {
      Converter.fromJsonUnsafe[JShort](JsNumber(42)) mustEqual (42.toShort: JShort)
    }
  }

  "The JBooleanJsonFormat" should {
    "convert true to a JsTrue" in { Converter.toJsonUnsafe[JBoolean](true: JBoolean) mustEqual JsTrue }
    "convert false to a JsFalse" in { Converter.toJsonUnsafe[JBoolean](false: JBoolean) mustEqual JsFalse }
    "convert a JsTrue to true" in { Converter.fromJsonUnsafe[JBoolean](JsTrue) mustEqual true }
    "convert a JsFalse to false" in { Converter.fromJsonUnsafe[JBoolean](JsFalse) mustEqual false }
  }

  "The JCharacterJsonFormat" should {
    "convert a JCharacter to a JsString" in {
      Converter.toJsonUnsafe[JCharacter]('c': JCharacter) mustEqual JsString("c")
    }
    "convert a JsString to a JCharacter" in {
      Converter.fromJsonUnsafe[JCharacter](JsString("c")) mustEqual ('c': JCharacter)
    }
  }
}
