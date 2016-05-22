/*
 * Original implementation (C) 2009-2011 Debasish Ghosh
 * Adapted and extended in 2011 by Mathias Doenitz
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

import annotation.implicitNotFound

/**
  * Provides the JSON deserialization for type A.
 */
@implicitNotFound(msg = "Cannot find JsonReader or JsonFormat type class for ${A}")
trait JsonReader[A] {
  def read[J](js: J, unbuilder: Unbuilder[J]): A
}

/**
  * Provides the JSON serialization for type A.
 */
@implicitNotFound(msg = "Cannot find JsonWriter or JsonFormat type class for ${A}")
trait JsonWriter[A] {
  def write[J](obj: A, builder: Builder[J]): Unit
}

/**
  * Provides the JSON deserialization and serialization for type A.
 */
trait JsonFormat[A] extends JsonReader[A] with JsonWriter[A]

/**
 * A special JsonReader capable of reading a legal JSON root object, i.e. either a JSON array or a JSON object.
 */
@implicitNotFound(msg = "Cannot find RootJsonReader or RootJsonFormat type class for ${A}")
trait RootJsonReader[A] extends JsonReader[A]

/**
 * A special JsonWriter capable of writing a legal JSON root object, i.e. either a JSON array or a JSON object.
 */
@implicitNotFound(msg = "Cannot find RootJsonWriter or RootJsonFormat type class for ${A}")
trait RootJsonWriter[A] extends JsonWriter[A]

/**
 * A special JsonFormat signaling that the format produces a legal JSON root object, i.e. either a JSON array
 * or a JSON object.
 */
trait RootJsonFormat[A] extends JsonFormat[A] with RootJsonReader[A] with RootJsonWriter[A]
