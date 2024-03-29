/*
 * Copyright (C) 2011 Mathias Doenitz
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

// package sjsonnew
// package support.spray

// import spray.json._

// object AdditionalFormatsSpec extends verify.BasicTestSuite {
//   case class Container[A](inner: Option[A])

//   object ReaderProtocol extends BasicJsonProtocol {
//     implicit def containerReader[T :JsonFormat]: JsonFormat[Container[T]] = liftFormat {
//       new JsonReader[Container[T]] {
//         def read(value: JsValue) = value match {
//           case JsObject(fields) if fields.contains("content") => Container(Some(jsonReader[T].read(fields("content"))))
//           case _ => deserializationError("Unexpected format: " + value.toString)
//         }
//       }
//     }
//   }

//   object WriterProtocol extends BasicJsonProtocol {
//     implicit def containerWriter[T :JsonFormat]: JsonFormat[Container[T]] = liftFormat {
//       new JsonWriter[Container[T]] {
//         def write(obj: Container[T]) = JsObject("content" -> obj.inner.toJson)
//       }
//     }
//   }

//   val obj = Container(Some(Container(Some(List(1, 2, 3)))))
//   test("The liftJsonWriter should properly write a Container[Container[List[Int]]] to JSON") {
//     import WriterProtocol._
//     Predef.assert(obj.toJson.toString == """{"content":{"content":[1,2,3]}}""")
//   }

//   test("The liftJsonWriter should properly read a Container[Container[List[Int]]] from JSON") {
//     import ReaderProtocol._
//     Predef.assert("""{"content":{"content":[1,2,3]}}""".parseJson.convertTo[Container[Container[List[Int]]]] == obj)
//   }

//   // case class Foo(id: Long, name: String, foos: Option[List[Foo]] = None)

//   // object FooProtocol extends DefaultJsonProtocol {
//   //   implicit val fooProtocol: JsonFormat[Foo] = lazyFormat(jsonFormat(Foo, "id", "name", "foos"))
//   // }

//   // "The lazyFormat wrapper" should {
//   //   "enable recursive format definitions" in {
//   //     import FooProtocol._
//   //     Foo(1, "a", Some(Foo(2, "b", Some(Foo(3, "c") :: Nil)) :: Foo(4, "d") :: Nil)).toJson.toString mustEqual
//   //       """{"id":1,"name":"a","foos":[{"id":2,"name":"b","foos":[{"id":3,"name":"c"}]},{"id":4,"name":"d"}]}"""
//   //   }
//   // }
// }
