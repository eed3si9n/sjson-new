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
package support.msgpack

import scala.util.Try
import scala.collection.mutable
import org.msgpack.core.MessagePack
import org.msgpack.value._
import java.io.{ InputStream, OutputStream }

object Converter extends SupportConverter[Value] {
  def toBinary[A](obj: A)(implicit writer: JsonWriter[A]): Try[Array[Byte]] =
    Try(toBinaryUnsafe(obj))
  def toBinary[A](obj: A, out: OutputStream)(implicit writer: JsonWriter[A]): Try[Unit] =
    Try(toBinaryUnsafe(obj, out))
  def toBinaryUnsafe[A](obj: A)(implicit writer: JsonWriter[A]): Array[Byte] =
    {
      val packer = MessagePack.newDefaultBufferPacker
      packer.packValue(toJsonUnsafe[A](obj))
      packer.toByteArray
    }
  def toBinaryUnsafe[A](obj: A, out: OutputStream)(implicit writer: JsonWriter[A]): Unit =
    {
      val packer = MessagePack.newDefaultPacker(out)
      packer.packValue(toJsonUnsafe[A](obj))
      packer.flush
    }
  def fromBinary[A](ary: Array[Byte])(implicit reader: JsonReader[A]): Try[A] =
    Try(fromBinaryUnsafe[A](ary))
  def fromBinary[A](in: InputStream)(implicit reader: JsonReader[A]): Try[A] =
    Try(fromBinaryUnsafe[A](in))
  def fromBinaryUnsafe[A](ary: Array[Byte])(implicit reader: JsonReader[A]): A =
    {
      val unpacker = MessagePack.newDefaultUnpacker(ary)
      val v = unpacker.unpackValue
      fromJsonUnsafe[A](v)
    }
  def fromBinaryUnsafe[A](in: InputStream)(implicit reader: JsonReader[A]): A =
    {
      val unpacker = MessagePack.newDefaultUnpacker(in)
      val v = unpacker.unpackValue
      fromJsonUnsafe[A](v)
    }

  implicit val facade: Facade[Value] = FacadeImpl
  private object FacadeImpl extends SimpleFacade[Value] {
    def jnull()               = ValueFactory.newNil
    def jfalse()              = ValueFactory.newBoolean(false)
    def jtrue()               = ValueFactory.newBoolean(true)
    def jnumstring(s: String) = ValueFactory.newString(s)
    def jintstring(s: String) = ValueFactory.newInteger(new java.math.BigInteger(s))
    def jint(i: Int)          = ValueFactory.newInteger(i)
    def jlong(l: Long)        = ValueFactory.newInteger(l)
    def jdouble(d: Double)    = ValueFactory.newFloat(d)
    def jbigdecimal(d: BigDecimal) = ValueFactory.newString(d.toString)
    def jstring(s: String)    = ValueFactory.newString(s)

    def jarray(vs: List[Value]): Value =
      {
        import scala.collection.JavaConverters._
        ValueFactory.newArray(vs.asJava)
      }
    def jobject(vs: Map[String, Value]): Value =
      {
        import scala.collection.JavaConverters._
        ValueFactory.newMap((vs map { case (k, v) => (ValueFactory.newString(k), v) }).asJava)
      }

    def isJnull(value: Value): Boolean =
      value.getValueType match {
        case ValueType.NIL => true
        case _             => false
      }
    def isObject(value: Value): Boolean =
      value.getValueType match {
        case ValueType.MAP => true
        case _             => false
      }
    def extractInt(value: Value): Int =
      value.getValueType match {
        case ValueType.INTEGER => value.asIntegerValue.asInt
        case _ => deserializationError("Expected Int as Integer, but got " + value)
      }
    def extractLong(value: Value): Long =
      value.getValueType match {
        case ValueType.INTEGER => value.asIntegerValue.asLong
        case _ => deserializationError("Expected Long as Integer, but got " + value)
      }
    def extractFloat(value: Value): Float =
      value.getValueType match {
        case ValueType.INTEGER => value.asIntegerValue.toFloat
        case ValueType.FLOAT   => value.asFloatValue.toFloat
        case ValueType.NIL     => Float.NaN
        case _ => deserializationError("Expected Float as Float, but got " + value)
      }
    def extractDouble(value: Value): Double =
      value.getValueType match {
        case ValueType.INTEGER => value.asIntegerValue.toDouble
        case ValueType.FLOAT   => value.asFloatValue.toDouble
        case ValueType.NIL     => Double.NaN
        case _ => deserializationError("Expected Double as Float, but got " + value)
      }
    def extractBigDecimal(value: Value): BigDecimal =
      value.getValueType match {
        case ValueType.STRING  => BigDecimal(value.asStringValue.asString)
        case ValueType.INTEGER => BigDecimal(value.asIntegerValue.toBigInteger)
        case ValueType.FLOAT   => BigDecimal(value.asFloatValue.toDouble)
        case _ => deserializationError("Expected BigDecimal as String, but got " + value)
      }
    def extractBoolean(value: Value): Boolean =
      value.getValueType match {
        case ValueType.BOOLEAN => value.asBooleanValue.getBoolean
        case _ => deserializationError("Expected Boolean, but got " + value)
      }
    def extractString(value: Value): String =
      value.getValueType match {
        case ValueType.STRING => value.asStringValue.asString
        case _ => deserializationError("Expected String as String, but got " + value)
      }
    def extractArray(value: Value): Vector[Value] =
      value.getValueType match {
        case ValueType.ARRAY =>
          import scala.collection.JavaConverters._
          value.asArrayValue.asScala.toVector
        case _ => deserializationError("Expected List as Array, but got " + value)
      }
    def extractObject(value: Value): (Map[String, Value], Vector[String]) =
      value.getValueType match {
        case ValueType.MAP =>
          import scala.collection.JavaConverters._
          val fs = value.asMapValue.map.asScala
          val names = (fs map { case (k, v) => k.toString }).toVector
          val fields = fs map { case (k, v) => (k.toString, v) }
          (fields.toMap, names)
        case _ => deserializationError("Expected Map as MMap, but got " + value)
      }
  }
}
