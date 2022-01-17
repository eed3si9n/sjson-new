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

import java.util.Arrays
import spray.json.{ JsArray, JsNumber, JsString, JsObject }
import LList._

object UnionFormatsSpec extends verify.BasicTestSuite with BasicJsonProtocol {
  sealed trait Fruit
  case class Apple() extends Fruit
  sealed trait Citrus extends Fruit
  case class Orange() extends Citrus
  implicit object AppleJsonFormat extends JsonFormat[Apple] {
    def write[J](x: Apple, builder: Builder[J]): Unit =
      {
        builder.beginObject()
        builder.addField("x", 0)
        builder.endObject()
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Apple =
      jsOpt match {
        case Some(js) =>
          val result = unbuilder.beginObject(js) match {
            case 1 =>
              val x = unbuilder.readField[Int]("x")
              if (x == 0) Apple()
              else deserializationError(s"Unexpected value: $x")
            case x => deserializationError(s"Unexpected number of fields: $x")
          }
          unbuilder.endObject()
          result
        case None => deserializationError("Expected JsNumber but found None")
      }
  }
  implicit object OrangeJsonFormat extends JsonFormat[Orange] {
    def write[J](x: Orange, builder: Builder[J]): Unit =
      {
        builder.beginObject()
        builder.addField("x", 1)
        builder.endObject()
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Orange =
      jsOpt match {
        case Some(js) =>
          val result = unbuilder.beginObject(js) match {
            case 1 =>
              val x = unbuilder.readField[Int]("x")
              if (x == 1) Orange()
              else deserializationError(s"Unexpected value: $x")
            case x => deserializationError(s"Unexpected number of fields: $x")
          }
          unbuilder.endObject()
          result
        case None => deserializationError("Expected JsNumber but found None")
      }
  }

  val fruit: Fruit = Apple()

  test("The unionFormat") {
    implicit val FruitFormat: JsonFormat[Fruit] = unionFormat2[Fruit, Apple, Orange]
    val fruitJson = JsObject("value" ->  JsObject("x" -> JsNumber(0)), "type" -> JsString("Apple"))
    // "convert a value of ADT to JObject"
    Predef.assert(Converter.toJsonUnsafe(fruit) == fruitJson)

    // "convert JObject back to ADT"
    Predef.assert(Converter.fromJsonUnsafe[Fruit](fruitJson) == fruit)
  }

  test("The flatUnionFormat") {
    implicit val FruitFormat: JsonFormat[Fruit] = flatUnionFormat2[Fruit, Apple, Orange]("type")
    val fruitJson2 = JsObject("type" -> JsString("Apple"), "x" -> JsNumber(0))
    // "convert a value of ADT to JObject"
    Predef.assert(Converter.toJsonUnsafe(fruit) == fruitJson2)

    // "convert JObject back to ADT"
    // println(Predef.assert(Converter.fromJsonUnsafe[Fruit](fruitJson2)))
    Predef.assert(Converter.fromJsonUnsafe[Fruit](fruitJson2) == fruit)
  }
}
