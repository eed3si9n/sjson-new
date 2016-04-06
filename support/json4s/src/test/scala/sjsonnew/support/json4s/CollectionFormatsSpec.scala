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
package support.json4s

import org.specs2.mutable._
import java.util.Arrays
import org.json4s.JsonAST._

class CollectionFormatsSpec extends Specification with BasicJsonProtocol {

  "The listFormat" should {
    val list = List(1, 2, 3)
    val json = JArray(List(JInt(1), JInt(2), JInt(3)))
    "convert a List[Int] to a JArray of JInts" in {
      Converter.toJsonUnsafe(list) mustEqual json
    }
    "convert a JArray of JInts to a List[Int]" in {
      Converter.fromJsonUnsafe[List[Int]](json) mustEqual list
    }
  }

  "The arrayFormat" should {
    val array = Array(1, 2, 3)
    val json = JArray(List(JInt(1), JInt(2), JInt(3)))
    "convert an Array[Int] to a JArray of JInts" in {
      Converter.toJsonUnsafe(array) mustEqual json
    }
    "convert a JArray of JInts to an Array[Int]" in {
      Arrays.equals(Converter.fromJsonUnsafe[Array[Int]](json), array) must beTrue
    }
  }

  "The mapFormat" should {
    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    val json = JObject(List("a" -> JInt(1), "b" -> JInt(2), "c" -> JInt(3)))
    "convert a Map[String, Long] to a JObject" in {
      Converter.toJsonUnsafe(map) mustEqual json
    }
    "be able to convert a JObject to a Map[String, Long]" in {
      Converter.fromJsonUnsafe[Map[String, Long]](json) mustEqual map
    }
    "throw an Exception when trying to serialize a map whose key are not serialized to JStrings" in {
      Converter.toJsonUnsafe(Map(1 -> "a")) must throwA(new SerializationException("sjsonnew.SerializationException: Unexpected builder state: InObject"))
    }
  }

}
