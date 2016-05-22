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

import scala.{Left, Right}

/**
  * Provides the JsonFormats for the non-collection standard types.
 */
trait StandardFormats {
  this: AdditionalFormats =>

  private[this] type JF[A] = JsonFormat[A] // simple alias for reduced verbosity

  implicit def optionFormat[A :JF]: JF[Option[A]] = new OptionFormat[A]

  class OptionFormat[A :JF] extends JF[Option[A]] {
    lazy val elemFormat = implicitly[JF[A]]
    def write[J](option: Option[A], builder: Builder[J]): Unit =
      option match {
        case Some(x) => elemFormat.write(x, builder)
        case None => builder.writeNull()
      }
    def read[J](js: J, unbuilder: Unbuilder[J]): Option[A] =
      if (unbuilder.isJnull(js)) None
      else Option(elemFormat.read(js, unbuilder))
  }

  implicit def eitherFormat[A :JF, B :JF] = new JF[Either[A, B]] {
    lazy val leftFormat = implicitly[JF[A]]
    lazy val rightFormat = implicitly[JF[B]]
    def write[J](either: Either[A, B], builder: Builder[J]): Unit =
      either match {
        case Left(a)  => leftFormat.write(a, builder)
        case Right(b) => rightFormat.write(b, builder)
      }
    def read[J](js: J, unbuilder: Unbuilder[J]): Either[A, B] =
      (safeReader[A].read(js, unbuilder), safeReader[B].read(js, unbuilder)) match {
        case (Right(a), _: Left[_, _]) => Left(a)
        case (_: Left[_, _], Right(b)) => Right(b)
        case (_: Right[_, _], _: Right[_, _]) => deserializationError("Ambiguous Either value: can be read as both, Left and Right, values")
        case (Left(ea), Left(eb)) => deserializationError("Could not read Either value:\n" + ea + "---------- and ----------\n" + eb)
      }
  }
}
