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

/** Same as LabelledGeneric in shapeless. */
trait IsoLList[A] {
  type R <: LList
  def to(a: A): R
  def from(r: R): A
  def jsonFormat: JsonFormat[R]
}

object IsoLList {
  type Aux[A, R0] = IsoLList[A]{ type R = R0 }
  def apply[A](implicit iso: IsoLList[A]): Aux[A, iso.R] = iso
  def iso[A, R0 <: LList: JsonFormat](to0: A => R0, from0: R0 => A): Aux[A, R0] = new IsoLList[A] {
    type R = R0
    def to(a: A): R = to0(a)
    def from(r: R): A = from0(r)
    val jsonFormat: JsonFormat[R] = implicitly[JsonFormat[R]]
  }
}
