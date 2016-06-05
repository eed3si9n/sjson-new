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

import org.specs2.mutable._
import java.util.Arrays
import spray.json.{ JsArray, JsNumber, JsString, JsObject }
import LList._

class UnionFormatsSpec extends Specification with BasicJsonProtocol {
  sealed trait Fruit
  case class Apple() extends Fruit
  case class Orange() extends Fruit
  implicit object AppleJsonFormat extends JsonFormat[Apple] {
    def write[J](x: Apple, builder: Builder[J]): Unit =
      builder.writeInt(0)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Apple =
      jsOpt match {
        case Some(js) =>
          unbuilder.readInt(js) match {
            case 0 => Apple()
            case x => deserializationError(s"Unexpected value: $x")
          }
        case None => deserializationError("Expected JsNumber but found None")
      }
  }
  implicit object OrangeJsonFormat extends JsonFormat[Orange] {
    def write[J](x: Orange, builder: Builder[J]): Unit =
      builder.writeInt(1)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Orange =
      jsOpt match {
        case Some(js) =>
          unbuilder.readInt(js) match {
            case 1 => Orange()
            case x => deserializationError(s"Unexpected value: $x")
          }
        case None => deserializationError("Expected JsNumber but found None")
      }
  }
  implicit val FruitFormat = unionFormat2[Fruit, Apple, Orange]

  val fruit: Fruit = Apple()
  val fruitJson = JsObject("value" -> JsNumber(0), "type" -> JsString("Apple"))
  "The unionFormat" should {
    "convert a value of ADT to JObject" in {
      Converter.toJsonUnsafe(fruit) mustEqual fruitJson
    }
    "convert JObject back to ADT" in {
      Converter.fromJsonUnsafe[Fruit](fruitJson) mustEqual fruit
    }
  }
}
