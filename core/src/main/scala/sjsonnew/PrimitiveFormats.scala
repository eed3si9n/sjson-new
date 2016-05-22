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
    def read[J](js: J, unbuilder: Unbuilder[J]): Int =
      unbuilder.readInt(js)
  }

  implicit object LongJsonFormat extends JsonFormat[Long] {
    def write[J](x: Long, builder: Builder[J]): Unit =
      builder.writeLong(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): Long =
      unbuilder.readLong(js)
  }

  implicit object FloatJsonFormat extends JsonFormat[Float] {
    def write[J](x: Float, builder: Builder[J]): Unit =
      builder.writeDouble(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): Float =
      unbuilder.readFloat(js)
  }

  implicit object DoubleJsonFormat extends JsonFormat[Double] {
    def write[J](x: Double, builder: Builder[J]): Unit =
      builder.writeDouble(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): Double =
      unbuilder.readDouble(js)
  }

  implicit object ByteJsonFormat extends JsonFormat[Byte] {
    def write[J](x: Byte, builder: Builder[J]): Unit =
      builder.writeInt(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): Byte =
      unbuilder.readInt(js).toByte
  }

  implicit object ShortJsonFormat extends JsonFormat[Short] {
    def write[J](x: Short, builder: Builder[J]): Unit =
      builder.writeInt(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): Short =
      unbuilder.readInt(js).toShort
  }

  implicit object BigDecimalJsonFormat extends JsonFormat[BigDecimal] {
    def write[J](x: BigDecimal, builder: Builder[J]): Unit =
      builder.writeBigDecimal(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): BigDecimal =
      unbuilder.readBigDecimal(js)
  }

  implicit object BigIntJsonFormat extends JsonFormat[BigInt] {
    def write[J](x: BigInt, builder: Builder[J]): Unit = {
      require(x ne null)
      builder.writeBigDecimal(BigDecimal(x))
    }
    def read[J](js: J, unbuilder: Unbuilder[J]): BigInt =
      unbuilder.readBigDecimal(js).toBigInt
  }

  implicit object UnitJsonFormat extends JsonFormat[Unit] {
    def write[J](x: Unit, builder: Builder[J]): Unit =
      builder.writeInt(1)
    def read[J](js: J, unbuilder: Unbuilder[J]): Unit = ()
  }

  implicit object BooleanJsonFormat extends JsonFormat[Boolean] {
    def write[J](x: Boolean, builder: Builder[J]): Unit =
      builder.writeBoolean(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): Boolean =
      unbuilder.readBoolean(js)
  }

  implicit object CharJsonFormat extends JsonFormat[Char] {
    def write[J](x: Char, builder: Builder[J]): Unit =
      builder.writeString(String.valueOf(x))
    def read[J](js: J, unbuilder: Unbuilder[J]): Char = {
      val str = unbuilder.readString(js)
      if (str.length == 1) str.charAt(0)
      else deserializationError("Expected Char as single-character JsString, but got " + str)
    }
  }

  implicit object StringJsonFormat extends JsonFormat[String] {
    def write[J](x: String, builder: Builder[J]): Unit =
      builder.writeString(x)
    def read[J](js: J, unbuilder: Unbuilder[J]): String =
      unbuilder.readString(js)
  }

  implicit object SymbolJsonFormat extends JsonFormat[Symbol] {
    def write[J](x: Symbol, builder: Builder[J]): Unit =
      builder.writeString(x.name)
    def read[J](js: J, unbuilder: Unbuilder[J]): Symbol = {
      val str = unbuilder.readString(js)
      Symbol(str)
    }
  }
}
