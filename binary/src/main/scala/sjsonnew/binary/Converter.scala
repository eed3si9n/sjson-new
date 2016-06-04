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
package binary

object Converter extends SupportConverter[BValue] {
  def toBinaryUnsafe[A](obj: A)(implicit writer: JsonWriter[A]): Array[Byte] =
    toJsonUnsafe[A](obj).toTopMessage.toBytes

  def fromBinaryUnsafe[A](ary: Array[Byte])(implicit reader: JsonReader[A]): A =
    {
      val message = BTopMessage.fromBytes(ary)
      // println(message.toBValue)
      fromJsonUnsafe[A](message.toBValue)
    }

  val facade: Facade[BValue] = FacadeImpl
  private object FacadeImpl extends SimpleFacade[BValue] {
    def jnull() = BNull
    def jfalse() = BSignedInt(0)
    def jtrue() = BSignedInt(1)
    def jdouble(d: Double) = BDouble(d)
    def jnumstring(s: String) = BNumber(s)
    def jbigdecimal(d: BigDecimal) = BNumber(d)
    def jintstring(s: String) = BNumber(s)
    def jint(i: Int) = BSignedInt(i)
    def jlong(l: Long) = BSignedLong(l)
    def jstring(s: String) = BString(s)
    def jarray(vs: List[BValue]) = BArray(vs.toVector)
    def jobject(vs: Map[String, BValue]) = BObject(vs)
    def isJnull(value: BValue): Boolean =
      value match {
        case BNull => true
        case _     => false
      }
    def isObject(value: BValue): Boolean =
      value match {
        case x: BObject     => true
        case x: BMessage    => true
        case x: BTopMessage => true
        case _              => false
      }
    def extractInt(value: BValue): Int =
      value match {
        case BSignedInt(x)   => x
        case BUnsignedInt(x) => x
        case BFixedInt(x)    => x
        case BSignedLong(x)  => x.toInt
        case BFixedLong(x)   => x.toInt
        case BDouble(x)      => x.toInt
        case BNumber(x)      => x.toInt
        case x => deserializationError("Expected Int as BSignedInt, but got " + x)
      }
    def extractLong(value: BValue): Long =
      value match {
        case BSignedInt(x)   => x.toLong
        case BUnsignedInt(x) => x.toLong
        case BFixedInt(x)    => x.toLong
        case BSignedLong(x)  => x
        case BFixedLong(x)   => x
        case BDouble(x)      => x.toLong
        case BNumber(x)      => x.toLong
        case x => deserializationError("Expected Long as BSignedLong, but got " + x)
      }
    def extractFloat(value: BValue): Float =
      value match {
        case BSignedInt(x)   => x.toFloat
        case BUnsignedInt(x) => x.toFloat
        case BFixedInt(x)    => x.toFloat
        case BSignedLong(x)  => x.toFloat
        case BFixedLong(x)   => x.toFloat
        case BDouble(x)      => x.toFloat
        case BNumber(x)      => x.toFloat
        case BNull           => Float.NaN
        case x => deserializationError("Expected Float as BDouble, but got " + x)
      }
    def extractDouble(value: BValue): Double =
      value match {
        case BSignedInt(x)   => x.toDouble
        case BUnsignedInt(x) => x.toDouble
        case BFixedInt(x)    => x.toDouble
        case BSignedLong(x)  => x.toDouble
        case BFixedLong(x)   => x.toDouble
        case BDouble(x)      => x
        case BNumber(x)      => x.toDouble
        case BNull           => Double.NaN
        case x => deserializationError("Expected Double as BDouble, but got " + x)
      }
    def extractBigDecimal(value: BValue): BigDecimal =
      value match {
        case BSignedInt(x)   => BigDecimal(x)
        case BUnsignedInt(x) => BigDecimal(x)
        case BFixedInt(x)    => BigDecimal(x)
        case BSignedLong(x)  => BigDecimal(x)
        case BFixedLong(x)   => BigDecimal(x)
        case BDouble(x)      => BigDecimal(x)
        case BNumber(x)      => BigDecimal(x)
        case x => deserializationError("Expected BigDecimal as BNumber, but got " + x)
      }
    def extractBoolean(value: BValue): Boolean =
      value match {
        case BSignedInt(x)   => x != 0
        case BUnsignedInt(x) => x != 0
        case x => deserializationError("Expected BSignedInt, but got " + x)
      }
    def extractString(value: BValue): String =
      value match {
        case BString(x) => x
        case x => deserializationError("Expected String as BString, but got " + x)
      }
    def extractArray(value: BValue): Vector[BValue] =
      value match {
        case BArray(elements) => elements
        case x => deserializationError("Expected List as BArray, but got " + x)
      }
    def extractObject(value: BValue): (Map[String, BValue], Vector[String]) =
      value match {
        case x: BObject =>
          val fields = x.fields
          val names = (x.fields map { case (k, v) => k }).toVector
          (fields, names)
        case x => deserializationError("Expected Map as BObject, but got " + x)
      }
  }
}
