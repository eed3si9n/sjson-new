/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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

import scala.reflect.ClassTag

package object sjsonnew
  extends PrimitiveFormats
  with StandardFormats
  with TupleFormats
  with CollectionFormats
  with AdditionalFormats
  with UnionFormats
  with FlatUnionFormats
  with IsoFormats
  with JavaPrimitiveFormats
  with ThrowableFormats
  with ImplicitHashWriters
 {
  def deserializationError(msg: String, cause: Throwable = null, fieldNames: List[String] = Nil) = throw new DeserializationException(msg, cause, fieldNames)
  def serializationError(msg: String) = throw new SerializationException(msg)

  def jsonReader[A](implicit reader: JsonReader[A]): JsonReader[A] = reader
  def jsonWriter[A](implicit writer: JsonWriter[A]): JsonWriter[A] = writer

  type LNil = LList.LNil0
  val LNil = LList.LNil0
  type :*:[A1, A2 <: LList] = LCons[A1, A2]
  object :*: {
    def apply[A1: JsonFormat: ClassTag, A2 <: LList: JsonFormat](name: String, head: A1, tail: A2): A1 :*: A2 =
      LCons(name, head, tail)

    def unapply[H, T <: LList](x: H :*: T): Some[((String, H), T)] = Some((x.name -> x.head, x.tail))
  }
}

package sjsonnew {
  case class DeserializationException(msg: String, cause: Throwable = null, fieldNames: List[String] = Nil) extends RuntimeException(msg, cause)
  class SerializationException(msg: String) extends RuntimeException(msg)
}
