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

trait IsoLListFormats {
  implicit def isolistFormat[A: IsoLList]: JsonFormat[A] =new JsonFormat[A] {
    val iso = implicitly[IsoLList[A]]
    def write[J](x: A, builder: Builder[J]): Unit =
      iso.jsonFormat.write(iso.to(x), builder)
    def read[J](js: J, unbuilder: Unbuilder[J]): A =
      iso.from(iso.jsonFormat.read(js, unbuilder))
  }
}
