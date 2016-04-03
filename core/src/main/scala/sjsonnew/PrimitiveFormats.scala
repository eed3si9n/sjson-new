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
    def write[J](x: Int, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jint(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Int =
      facade.extractInt(js)
  }

  implicit object LongJsonFormat extends JsonFormat[Long] {
    def write[J](x: Long, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jlong(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Long =
      facade.extractLong(js)
  }

  implicit object FloatJsonFormat extends JsonFormat[Float] {
    def write[J](x: Float, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jnum(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Float =
      facade.extractFloat(js)
  }

  implicit object DoubleJsonFormat extends JsonFormat[Double] {
    def write[J](x: Double, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jnum(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Double =
      facade.extractDouble(js)
  }

  implicit object ByteJsonFormat extends JsonFormat[Byte] {
    def write[J](x: Byte, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jnum(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Byte =
      facade.extractInt(js).toByte
  }

  implicit object ShortJsonFormat extends JsonFormat[Short] {
    def write[J](x: Short, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jnum(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Short =
      facade.extractInt(js).toShort
  }

  implicit object BigDecimalJsonFormat extends JsonFormat[BigDecimal] {
    def write[J](x: BigDecimal, builder: Builder[J], facade: Facade[J]): Unit = {
      require(x ne null)
      val context = facade.singleContext()
      context.add(facade.jnum(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): BigDecimal =
      facade.extractBigDecimal(js)
  }

  implicit object BigIntJsonFormat extends JsonFormat[BigInt] {
    def write[J](x: BigInt, builder: Builder[J], facade: Facade[J]): Unit = {
      require(x ne null)
      val context = facade.singleContext()
      context.add(facade.jnum(BigDecimal(x)))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): BigInt =
      facade.extractBigDecimal(js).toBigInt
  }

  implicit object UnitJsonFormat extends JsonFormat[Unit] {
    def write[J](x: Unit, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jint(1))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Unit = ()
  }

  implicit object BooleanJsonFormat extends JsonFormat[Boolean] {
    def write[J](x: Boolean, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      if (x) context.add(facade.jtrue())
      else context.add(facade.jfalse())
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Boolean =
      facade.extractBoolean(js)
  }

  implicit object CharJsonFormat extends JsonFormat[Char] {
    def write[J](x: Char, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jstring(String.valueOf(x)))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Char = {
      val str = facade.extractString(js)
      if (str.length == 1) str.charAt(0)
      else deserializationError("Expected Char as single-character JsString, but got " + str)
    }
  }

  implicit object StringJsonFormat extends JsonFormat[String] {
    def write[J](x: String, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jstring(x))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): String =
      facade.extractString(js)
  }

  implicit object SymbolJsonFormat extends JsonFormat[Symbol] {
    def write[J](x: Symbol, builder: Builder[J], facade: Facade[J]): Unit = {
      val context = facade.singleContext()
      context.add(facade.jstring(x.name))
      builder.add(context)
    }
    def read[J](js: J, facade: Facade[J]): Symbol = {
      val str = facade.extractString(js)
      Symbol(str)
    }
  }
}
