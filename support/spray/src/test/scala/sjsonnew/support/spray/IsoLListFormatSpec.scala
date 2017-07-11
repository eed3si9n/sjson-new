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

import spray.json.{ JsArray, JsNumber, JsString, JsObject }
import LList._
import org.specs2.mutable.Specification

class IsoLListFormatSpec extends Specification with BasicJsonProtocol {
  sealed trait Contact
  case class Person(name: String, value: Option[Int]) extends Contact
  case class Organization(name: String, value: Option[Int]) extends Contact

  implicit val personIso: IsoLList.Aux[Person, String :*: Option[Int] :*: LNil] = LList.iso2(
    { p: Person => ("name", p.name) :*: ("value", p.value) :*: LNil })
    { in => Person(
      in.find[String]("name").get,
      in.find[Option[Int]]("value").flatten) }

  implicit val organizationIso: IsoLList.Aux[Organization, String :*: Option[Int] :*: LNil] = LList.iso2(
    { o: Organization => ("name", o.name) :*: ("value", o.value) :*: LNil })
    { in => Organization(
      in.find[String]("name").get,
      in.find[Option[Int]]("value").flatten) }

  implicit val ContactFormat: JsonFormat[Contact] = flatUnionFormat2[Contact, Person, Organization]("$type")

  val p1 = Person("Alice", Some(1))
  val personJs = JsObject("$fields" -> JsArray(JsString("name"), JsString("value")),
    "name" -> JsString("Alice"), "value" -> JsNumber(1))
  val c1: Contact = Organization("Company", None)
  val contactJs =
    JsObject(
      "$type" -> JsString("Organization"),
      "$fields" -> JsArray(JsString("name"), JsString("value")),
      "name" -> JsString("Company")
    )
  "The isomorphism from a custom type to LList" should {
    "convert from value to JObject" in {
      Converter.toJsonUnsafe(p1) mustEqual personJs
    }
    "convert from JObject to the same value" in {
      Converter.fromJsonUnsafe[Person](personJs) mustEqual p1
    }
    "convert from a union value to JObject" in {
      Converter.toJsonUnsafe(c1) mustEqual contactJs
    }
  }
}
