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

trait IsoString[A] {
  def to(a: A): String
  def from(s: String): A
}

object IsoString {
  def iso[A](to0: A => String, from0: String => A): IsoString[A] = new IsoString[A] {
    def to(a: A): String = to0(a)
    def from(s: String): A = from0(s)
  }
}
