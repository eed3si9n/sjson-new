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

trait ImplicitHashWriters {
  implicit def implicitHashWriter[A](implicit jsonWriter: JsonWriter[A]): HashWriter[A] =
    new HashWriter[A] {
      override def write[J](obj: A, builder: Builder[J]): Unit = jsonWriter.write(obj, builder)
      override def addField[J](name: String, obj: A, builder: Builder[J]): Unit = jsonWriter.addField(name, obj, builder)
    }
}
