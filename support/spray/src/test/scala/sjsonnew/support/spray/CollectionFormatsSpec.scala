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

import org.specs2.mutable._
import java.util.Arrays
import spray.json.{ JsArray, JsNumber, JsString, JsObject }

class CollectionFormatsSpec extends Specification with BasicJsonProtocol {

  "The listFormat" should {
    val list = List(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a List[Int] to a JsArray of JsNumbers" in {
      Converter.toJsonUnsafe(list) mustEqual json
    }
    "convert a JsArray of JsNumbers to a List[Int]" in {
      Converter.fromJsonUnsafe[List[Int]](json) mustEqual list
    }
  }

  "The arrayFormat" should {
    val array = Array(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert an Array[Int] to a JsArray of JsNumbers" in {
      Converter.toJsonUnsafe(array) mustEqual json
    }
    "convert a JsArray of JsNumbers to an Array[Int]" in {
      Arrays.equals(Converter.fromJsonUnsafe[Array[Int]](json), array) must beTrue
    }
  }

  "The mapFormat" should {
    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    val json = JsObject("a" -> JsNumber(1), "b" -> JsNumber(2), "c" -> JsNumber(3))
    "convert a Map[String, Long] to a JsObject" in {
      Converter.toJsonUnsafe(map) mustEqual json
    }
    "be able to convert a JsObject to a Map[String, Long]" in {
      Converter.fromJsonUnsafe[Map[String, Long]](json) mustEqual map
    }
    "throw an Exception when trying to serialize a map whose key are not serialized to JsStrings" in {
      Converter.toJsonUnsafe(Map(1 -> "a")) must throwA(new SerializationException("Map key must be formatted as JString, not '1'"))
    }
  }

  "The immutableSetFormat" should {
    val set = Set(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a Set[Int] to a JsArray of JsNumbers" in {
      Converter.toJsonUnsafe(set) mustEqual json
    }
    "convert a JsArray of JsNumbers to a Set[Int]" in {
      Converter.fromJsonUnsafe[Set[Int]](json) mustEqual set
    }
  }

  "The indexedSeqFormat" should {
    val seq = collection.IndexedSeq(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a Set[Int] to a JsArray of JsNumbers" in {
      Converter.toJsonUnsafe(seq) mustEqual json
    }
    "convert a JsArray of JsNumbers to a IndexedSeq[Int]" in {
      Converter.fromJsonUnsafe[collection.IndexedSeq[Int]](json) mustEqual seq
    }
  }
}