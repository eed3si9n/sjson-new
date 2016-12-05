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
import spray.json.{ JsArray, JsNumber, JsObject, JsString, JsValue }

class CollectionFormatsSpec extends Specification with BasicJsonProtocol {
  case class Person(name: String, value: List[Int], ary: Array[Int],
    m: Map[String, Int], vs: Vector[Int])
  implicit object PersonFormat extends JsonFormat[Person] {
    def write[J](x: Person, builder: Builder[J]): Unit = {
      builder.beginObject()
      builder.addField("name", x.name)
      builder.addField("value", x.value)
      builder.addField("ary", x.ary)
      builder.addField("m", x.m)
      builder.addField("vs", x.vs)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Person =
      jsOpt match {
        case Some(js) =>
          unbuilder.beginObject(js)
          val name = unbuilder.readField[String]("name")
          val value = unbuilder.readField[List[Int]]("value")
          val ary = unbuilder.readField[Array[Int]]("ary")
          val m = unbuilder.readField[Map[String, Int]]("m")
          val vs = unbuilder.readField[Vector[Int]]("vs")
          unbuilder.endObject()
          Person(name, value, ary, m, vs)
        case None =>
          deserializationError("Expected JsObject but found None")
      }
  }
  val person = Person("x", Nil, Array(), Map(), Vector())
  val personJson = JsObject("name" -> JsString("x"))

  case class Peep(name: String)
  implicit object PeepFormat extends JsonFormat[Peep] {
    def write[J](x: Peep, builder: Builder[J]) = {
      builder.beginObject()
      builder.addField("name", x.name)
      builder.endObject()
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]) = jsOpt match {
      case Some(js) =>
        unbuilder.beginObject(js)
        val name = unbuilder.readField[String]("name")
        unbuilder.endObject()
        Peep(name)
      case None => deserializationError("Expected JsObject but found None")
    }
  }
  implicit val PeepKeyFormat: JsonKeyFormat[Peep] = JsonKeyFormat(_.name, Peep)
  val peep = Peep("x")

  "The listFormat" should {
    val list = List(1, 2, 3)
    val json = JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
    "convert a List[Int] to a JsArray of JsNumbers" in {
      Converter.toJsonUnsafe(list) mustEqual json
    }
    "convert a JsArray of JsNumbers to a List[Int]" in {
      Converter.fromJsonUnsafe[List[Int]](json) mustEqual list
    }
    "omit Nil fields" in {
      Converter.toJsonUnsafe(person) mustEqual personJson
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
    "round trip a Map[Peep, Int]" in assertRoundTrip(Map(peep -> 1))
    // "throw an Exception when trying to serialize a map whose key are not serialized to JsStrings" in {
    //   Converter.toJsonUnsafe(Map(1 -> "a")) must throwA(new SerializationException("Unexpected builder state: InObject"))
    // }
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

  def assertRoundTrip[A: JsonWriter: JsonReader](x: A) = {
    val jValue: JsValue = Converter.toJsonUnsafe(x)
    val y: A = Converter.fromJsonUnsafe[A](jValue)
    x mustEqual y
  }
}
