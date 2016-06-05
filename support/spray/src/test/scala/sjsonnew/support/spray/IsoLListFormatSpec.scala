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

  implicit val personIso = LList.iso(
    { p: Person => ("name", p.name) :*: ("value", p.value) :*: LNil },
    { in: String :*: Option[Int] :*: LNil => Person(
      in.find[String]("name").get,
      in.find[Option[Int]]("value").flatten) })
  implicit val organizationIso = LList.iso(
    { o: Organization => ("name", o.name) :*: ("value", o.value) :*: LNil },
    { in: String :*: Option[Int] :*: LNil => Organization(
      in.find[String]("name").get,
      in.find[Option[Int]]("value").flatten) })
  implicit val ContactFormat = unionFormat2[Contact, Person, Organization]
  val p1 = Person("Alice", Some(1))
  val personJs = JsObject("name" -> JsString("Alice"), "value" -> JsNumber(1))
  val c1: Contact = Organization("Company", None)
  val contactJs =
    JsObject("value" -> JsObject("name" -> JsString("Company")),
      "type" -> JsString("Organization"))
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
