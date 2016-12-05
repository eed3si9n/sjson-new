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

/**
  * Provides the JsonFormats for the most important Scala types.
 */
trait PrimitiveFormats {
  implicit object IntJsonFormat extends JsonFormat[Int] {
    def write[J](x: Int, builder: Builder[J]): Unit =
      builder.writeInt(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Int =
      jsOpt match {
        case Some(js) => unbuilder.readInt(js)
        case None     => 0
      }
  }

  implicit object LongJsonFormat extends JsonFormat[Long] {
    def write[J](x: Long, builder: Builder[J]): Unit =
      builder.writeLong(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Long =
      jsOpt match {
        case Some(js) => unbuilder.readLong(js)
        case None     => 0L
      }
  }

  implicit object FloatJsonFormat extends JsonFormat[Float] {
    def write[J](x: Float, builder: Builder[J]): Unit =
      builder.writeDouble(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Float =
      jsOpt match {
        case Some(js) => unbuilder.readFloat(js)
        case None     => 0.0f
      }
  }

  implicit object DoubleJsonFormat extends JsonFormat[Double] {
    def write[J](x: Double, builder: Builder[J]): Unit =
      builder.writeDouble(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Double =
      jsOpt match {
        case Some(js) => unbuilder.readDouble(js)
        case None     => 0.0
      }
  }

  implicit object ByteJsonFormat extends JsonFormat[Byte] {
    def write[J](x: Byte, builder: Builder[J]): Unit =
      builder.writeInt(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Byte =
      jsOpt match {
        case Some(js) => unbuilder.readInt(js).toByte
        case None     => 0.toByte
      }
  }

  implicit object ShortJsonFormat extends JsonFormat[Short] {
    def write[J](x: Short, builder: Builder[J]): Unit =
      builder.writeInt(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Short =
      jsOpt match {
        case Some(js) => unbuilder.readInt(js).toShort
        case None     => 0.toShort
      }
  }

  implicit object BigDecimalJsonFormat extends JsonFormat[BigDecimal] {
    def write[J](x: BigDecimal, builder: Builder[J]): Unit =
      builder.writeBigDecimal(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): BigDecimal =
      jsOpt match {
        case Some(js) => unbuilder.readBigDecimal(js)
        case None     => BigDecimal(0)
      }
  }

  implicit object BigIntJsonFormat extends JsonFormat[BigInt] {
    def write[J](x: BigInt, builder: Builder[J]): Unit = {
      require(x ne null)
      builder.writeBigDecimal(BigDecimal(x))
    }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): BigInt =
      jsOpt match {
        case Some(js) => unbuilder.readBigDecimal(js).toBigInt
        case None     => BigInt(0)
      }
  }

  implicit object UnitJsonFormat extends JsonFormat[Unit] {
    def write[J](x: Unit, builder: Builder[J]): Unit =
      builder.writeInt(1)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Unit = ()
  }

  implicit object BooleanJsonFormat extends JsonFormat[Boolean] {
    def write[J](x: Boolean, builder: Builder[J]): Unit =
      builder.writeBoolean(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Boolean =
      jsOpt match {
        case Some(js) => unbuilder.readBoolean(js)
        case None     => false
      }
  }

  implicit object CharJsonFormat extends JsonFormat[Char] {
    def write[J](x: Char, builder: Builder[J]): Unit =
      builder.writeString(String.valueOf(x))
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Char =
      jsOpt match {
        case Some(js) =>
          val str = unbuilder.readString(js)
          if (str.length == 1) str.charAt(0)
          else deserializationError("Expected Char as single-character JsString, but got " + str)
        case None => deserializationError("Expected Char as single-character JsString, but got None")
      }
  }

  implicit object StringJsonFormat extends JsonFormat[String] {
    def write[J](x: String, builder: Builder[J]): Unit =
      builder.writeString(x)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): String =
      jsOpt match {
        case Some(js) => unbuilder.readString(js)
        case None     => ""
      }
  }

  implicit object SymbolJsonFormat extends JsonFormat[Symbol] {
    def write[J](x: Symbol, builder: Builder[J]): Unit =
      builder.writeString(x.name)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Symbol =
      jsOpt match {
        case Some(js) =>
          val str = unbuilder.readString(js)
          Symbol(str)
        case None => deserializationError("Expected Symbol as JsString, but got None")
      }
  }

  implicit val StringJsonKeyFormat: JsonKeyFormat[String]         = JsonKeyFormat(identity, identity)
  implicit val SymbolJsonKeyFormat: JsonKeyFormat[Symbol]         = JsonKeyFormat(_.name, Symbol(_))

  implicit val UnitJsonKeyFormat: JsonKeyFormat[Unit]             = JsonKeyFormat(_ => "\"\"", _ => ())
  implicit val BooleanJsonKeyFormat: JsonKeyFormat[Unit]          = JsonKeyFormat(_.toString, _.toBoolean)
  implicit val ByteJsonKeyFormat: JsonKeyFormat[Byte]             = JsonKeyFormat(_.toString, _.toByte)
  implicit val ShortJsonKeyFormat: JsonKeyFormat[Short]           = JsonKeyFormat(_.toString, _.toShort)
  implicit val CharJsonKeyFormat: JsonKeyFormat[Char]             = JsonKeyFormat(_.toString, _.head)
  implicit val IntJsonKeyFormat: JsonKeyFormat[Int]               = JsonKeyFormat(_.toString, _.toInt)
  implicit val LongJsonKeyFormat: JsonKeyFormat[Long]             = JsonKeyFormat(_.toString, _.toLong)
  implicit val FloatJsonKeyFormat: JsonKeyFormat[Float]           = JsonKeyFormat(_.toString, _.toFloat)
  implicit val DoubleJsonKeyFormat: JsonKeyFormat[Double]         = JsonKeyFormat(_.toString, _.toDouble)

  implicit val BigDecimalJsonKeyFormat: JsonKeyFormat[BigDecimal] = JsonKeyFormat(_.toString, BigDecimal(_))
  implicit val BigIntJsonKeyFormat: JsonKeyFormat[BigInt]         = JsonKeyFormat(_.toString, BigInt(_))
}
