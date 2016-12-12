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

import annotation.implicitNotFound

/**
  * Provides hashing for type A.
 */
@implicitNotFound(msg = "Cannot find HashWriter type class for ${A}")
trait HashWriter[A] {
  def write[J](obj: A, builder: Builder[J]): Unit
  def addField[J](name: String, obj: A, builder: Builder[J]): Unit =
    {
      builder.addFieldName(name)
      write(obj, builder)
    }
}
