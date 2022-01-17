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

import java.lang.{ Integer => JInteger, Long => JLong, Boolean => JBoolean,
  Float => JFloat, Double => JDouble, Byte => JByte, Short => JShort,
  Character => JCharacter }

trait JavaPrimitiveFormats {
  this: PrimitiveFormats with AdditionalFormats with IsoFormats with StandardFormats =>

  private[this] type JF[A] = JsonFormat[A] // simple alias for reduced verbosity

  implicit lazy val JIntegerJsonFormat: JsonFormat[JInteger] =
    projectFormat(
      (x: JInteger) => if (x == null) None else Some(x: Int),
      (x: Option[Int]) => {
        x match {
          case Some(x) => x
          case None    => 0
        }
      }
    )

  implicit lazy val JLongJsonFormat: JsonFormat[JLong] =
    projectFormat(
      (x: JLong) => if (x == null) None else Some(x: Long),
      (x: Option[Long]) => {
        x match {
          case Some(x) => x
          case None    => 0L
        }
      }
    )

  implicit lazy val JFloatJsonFormat: JsonFormat[JFloat] =
    projectFormat(
      (x: JFloat) => if (x == null) None else Some(x: Float),
      (x: Option[Float]) => {
        x match {
          case Some(x) => x
          case None    => 0.0f
        }
      }
    )

  implicit lazy val JDoubleJsonFormat: JsonFormat[JDouble] =
    projectFormat(
      (x: JDouble) => if (x == null) None else Some(x: Double),
      (x: Option[Double]) => {
        x match {
          case Some(x) => x
          case None    => 0.0
        }
      }
    )

  implicit lazy val JByteJsonFormat: JsonFormat[JByte] =
    projectFormat(
      (x: JByte) => if (x == null) None else Some(x: Byte),
      (x: Option[Byte]) => {
        x match {
          case Some(x) => x
          case None    => 0.toByte
        }
      }
    )

  implicit lazy val JShortJsonFormat: JsonFormat[JShort] =
    projectFormat(
      (x: JShort) => if (x == null) None else Some(x: Short),
      (x: Option[Short]) => {
        x match {
          case Some(x) => x
          case None    => 0.toShort
        }
      }
    )

  implicit lazy val JBooleanJsonFormat: JsonFormat[JBoolean] =
    projectFormat(
      (x: JBoolean) => if (x == null) None else Some(x: Boolean),
      (x: Option[Boolean]) => {
        x match {
          case Some(x) => x
          case None    => false
        }
      }
    )

  implicit lazy val JCharacterJsonFormat: JsonFormat[JCharacter] =
    projectFormat(
      (x: JCharacter) => if (x == null) None else Some(x: Char),
      (x: Option[Char]) => {
        x match {
          case Some(x) => x
          case None    => deserializationError("Expected Char as single-character JsString, but got None")
        }
      }
    )

  implicit val JBooleanJsonKeyFormat: JsonKeyFormat[JBoolean]     = JsonKeyFormat(_.toString, _.toBoolean)
  implicit val JByteJsonKeyFormat: JsonKeyFormat[JByte]           = JsonKeyFormat(_.toString, _.toByte)
  implicit val JShortJsonKeyFormat: JsonKeyFormat[JShort]         = JsonKeyFormat(_.toString, _.toShort)
  implicit val JCharacterJsonKeyFormat: JsonKeyFormat[JCharacter] = JsonKeyFormat(_.toString, _.head)
  implicit val JIntegerJsonKeyFormat: JsonKeyFormat[JInteger]     = JsonKeyFormat(_.toString, _.toInt)
  implicit val JLongJsonKeyFormat: JsonKeyFormat[JLong]           = JsonKeyFormat(_.toString, _.toLong)
  implicit val JFloatJsonKeyFormat: JsonKeyFormat[JFloat]         = JsonKeyFormat(_.toString, _.toFloat)
  implicit val JDoubleJsonKeyFormat: JsonKeyFormat[JDouble]       = JsonKeyFormat(_.toString, _.toDouble)
}
