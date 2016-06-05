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

import scala.collection.immutable.SortedMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import java.nio.{ ByteBuffer, ByteOrder }
import java.io.{ ByteArrayOutputStream, ByteArrayInputStream, DataOutputStream, DataInputStream }

/** Represents binary wire types. */
sealed trait BType {
  def wireTypeByte: Byte
  def parse(buf: ByteBuffer): (BValue, Int)
}
// The top bit has to be 0x0 because of Varint
object BType {
  def apply(b: Byte): BType =
    b match {
      case 0x0 => BNull
      case 0x1 => BSignedInt
      case 0x2 => BUnsignedInt
      case 0x3 => BSignedLong
      case 0x4 => BDouble
      case 0x5 => BFixedInt
      case 0x6 => BFixedLong
      case 0x7 => BString
      case 0x8 => BMessage
      case 0x9 => BArray
      case 0xA => BFieldName
      case 0xB => BNumber
    }
  lazy val lengthFieldNameAsInt: Int = BFieldName.wireTypeByte.toInt
}

sealed trait BValue {
  def toBytes: Array[Byte]
  def wireType: BType
  def unpackMessage(fieldNames: SortedMap[Int, String]): BValue = this
  def toTopMessage: BTopMessage = BObject(Map("*" -> this)).toTopMessage
}

/** Represents null.
 * Expressed as unsigned varint 0.
 */
case object BNull extends BValue with BType {
  val wireTypeByte: Byte = 0x0
  val wireType: BType = this
  def toBytes: Array[Byte] = Varint.writeUnsignedVarInt(0)
  def parse(buf: ByteBuffer): (BValue, Int) =
    {
      buf.get
      (BNull, 1)
    }
}

/** Represents signed int32.
 * Expressed as signed varint.
 */
case class BSignedInt(value: Int) extends BValue {
  val wireType: BType = BSignedInt
  def toBytes: Array[Byte] = Varint.writeSignedVarInt(value)
}

object BSignedInt extends BType {
  val wireTypeByte: Byte = 0x1
  def parse(buf: ByteBuffer): (BSignedInt, Int) =
    {
      val ary = BUtil.readVarIntBytes(buf)
      (BSignedInt(Varint.readSignedVarInt(ary)), ary.length)
    }
}

/** Represents unsigned int32.
 * Expressed as unsigned varint.
 */
case class BUnsignedInt(value: Int) extends BValue {
  val wireType: BType = BUnsignedInt
  def toBytes: Array[Byte] = Varint.writeUnsignedVarInt(value)
}

object BUnsignedInt extends BType {
  val wireTypeByte: Byte = 0x2
  def parse(buf: ByteBuffer): (BUnsignedInt, Int) =
    {
      val ary = BUtil.readVarIntBytes(buf)
      (BUnsignedInt(Varint.readUnsignedVarInt(ary)), ary.length)
    }
}

/** Represents signed int64.
 * Expressed as signed varint.
 */
case class BSignedLong(value: Long) extends BValue {
  val wireType: BType = BSignedLong
  def toBytes: Array[Byte] = {
      val baos = new ByteArrayOutputStream
      val data = new DataOutputStream(baos)
      Varint.writeSignedVarLong(value, data)
      data.flush()
      baos.toByteArray
    }
}

object BSignedLong extends BType {
  val wireTypeByte: Byte = 0x3
  def parse(buf: ByteBuffer): (BSignedLong, Int) =
    {
      val ary = BUtil.readVarIntBytes(buf)
      val bais = new ByteArrayInputStream(ary)
      val data = new DataInputStream(bais)
      (BSignedLong(Varint.readSignedVarLong(data)), ary.length)
    }
}

/** Represents 64bit IEEE floating point.
 * Expressed as fixed-width in little endian byte order.
 */
class BDouble private[sjsonnew] (val value: Double) extends BValue {
  val wireType: BType = BDouble
  def toBytes: Array[Byte] =
    {
      val buf = ByteBuffer.allocate(8)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      buf.putDouble(value)
      buf.array
    }
}

object BDouble extends BType {
  val wireTypeByte: Byte = 0x4
  def apply(value: Double): BValue =
    if (value.isNaN) BNull
    else if (value.isInfinity) BNull
    else new BDouble(value)
  def unapply(x: BDouble): Option[Double] = Some(x.value)

  def parse(buf: ByteBuffer): (BValue, Int) =
    {
      val d = buf.getDouble
      (BDouble(d), 8)
    }
}

/** Represents int32.
 * Expressed as fixed-width Int in little endian byte order.
 */
case class BFixedInt(val value: Int) extends BValue {
  val wireType: BType = BFixedInt
  def toBytes: Array[Byte] =
    {
      val buf = ByteBuffer.allocate(4)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      buf.putInt(value)
      buf.array
    }
}

object BFixedInt extends BType {
  val wireTypeByte: Byte = 0x5
  def parse(buf: ByteBuffer): (BFixedInt, Int) =
    {
      val d = buf.getInt
      (BFixedInt(d), 4)
    }
}

/** Represents int64.
 * Expressed as fixed-width Long in little endian byte order.
 */
case class BFixedLong(val value: Long) extends BValue {
  val wireType: BType = BFixedLong
  def toBytes: Array[Byte] =
    {
      val buf = ByteBuffer.allocate(8)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      buf.putLong(value)
      buf.array
    }
}

object BFixedLong extends BType {
  val wireTypeByte: Byte = 0x6
  def parse(buf: ByteBuffer): (BFixedLong, Int) =
    {
      val d = buf.getLong
      (BFixedLong(d), 8)
    }
}

/** Represents a String.
 * Expressed as length delimited UTF-8 bytes.
 */
case class BString(value: String) extends BValue {
  val wireType: BType = BString
  def toBytes: Array[Byte] =
    {
      val buf = new ArrayBuffer[Byte]
      val contents = value.getBytes("UTF-8")
      buf.appendAll(BUnsignedInt(contents.length).toBytes)
      buf.appendAll(contents)
      buf.toArray
    }
}

object BString extends BType {
  val wireTypeByte: Byte = 0x7
  def parse(buf: ByteBuffer): (BString, Int) =
    {
      val (len, inc) = BUnsignedInt.parse(buf)
      val xs = new Array[Byte](len.value)
      buf.get(xs)
      (BString(new String(xs, "UTF-8")), len.value + inc)
    }
}

/** Represents a number of arbitrary precision, similar to JSON number.
 * Expressed as length delimited UTF-8 bytes of the string representation.
 */
case class BNumber(value: String) extends BValue {
  def wireType: BType = BNumber
  def toBytes: Array[Byte] =
    {
      val buf = new ArrayBuffer[Byte]
      val contents = value.getBytes("UTF-8")
      buf.appendAll(BUnsignedInt(contents.length).toBytes)
      buf.appendAll(contents)
      buf.toArray
    }
}

object BNumber extends BType {
  val wireTypeByte: Byte = 0xB
  def apply(value: BigDecimal): BNumber =
    BNumber(value.toString)

  def parse(buf: ByteBuffer): (BNumber, Int) =
    {
      val (len, inc) = BUnsignedInt.parse(buf)
      val xs = new Array[Byte](len.value)
      buf.get(xs)
      (BNumber(new String(xs, "UTF-8")), len.value + inc)
    }
}

/** Represents a list of `BValue`s, similar to JSON array.
 * Expressed in a similar binary layout as BMessage.
 */
case class BArray(values: Vector[BValue]) extends BValue {
  val wireType: BType = BArray
  def toBytes: Array[Byte] =
    {
      val fieldBytes = values.zipWithIndex map { case (v, i) =>
        val idx = ((i + 1) << 4) | v.wireType.wireTypeByte
        (idx, v.toBytes)
      }
      val fieldSize = (0 /: fieldBytes) { _ + _._2.length + 4 }
      val fieldSizeBytes = BUnsignedInt(fieldSize).toBytes
      val size = {
        fieldSize + fieldSizeBytes.length
      }
      val buf = ByteBuffer.allocate(size)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      buf.put(fieldSizeBytes)
      fieldBytes foreach { case (k, v) =>
        buf.putInt(k)
        buf.put(v)
      }
      buf.array
    }
  private[sjsonnew] def toMessageArray(fieldNames: mutable.Map[Int, String]): BArray =
    BArray(values map {
      case x: BObject => x.toMessage(fieldNames)
      case x: BArray  => x.toMessageArray(fieldNames)
      case x          => x
    })
  private[sjsonnew] def toObjectArray(fieldNames: SortedMap[Int, String]): BArray =
    BArray(values map {
      case x: BMessage => x.toObject(fieldNames)
      case x: BArray   => x.toObjectArray(fieldNames)
      case x           => x
    })
}

object BArray extends BType {
  val wireTypeByte: Byte = 0x9
  def parse(buf: ByteBuffer): (BArray, Int) =
    {
      val (len, inc) = BUnsignedInt.parse(buf)
      val xs = new Array[Byte](len.value)
      buf.get(xs)
      val obj = BMessage.fromBytes(xs)
      (BArray(obj.fields.toVector map { case (k, v) => v }), len.value + inc)
    }
}

/** Represents a String-indexed message, similar to JSON object.
  * During serialization this is converted to a `BMessage`, which is indexed using Int.
  */
case class BObject(fields: Map[String, BValue]) extends BValue {
  val wireType: BType = BMessage
  // This shouldn't be called
  def toBytes: Array[Byte] = serializationError("Unexpected call to toBytes on BObject")
  override def toTopMessage: BTopMessage =
    {
      val fieldNames = mutable.Map.empty[Int, String]
      val msg0 = toMessage(fieldNames)
      val msg =
        if (fieldNames.isEmpty) msg0
        else BMessage(msg0.fields ++
           Vector(BType.lengthFieldNameAsInt -> BFieldName(SortedMap(fieldNames.toSeq: _*))))
      BTopMessage(msg)
    }

  private[sjsonnew] def toMessage(fieldNames: mutable.Map[Int, String]): BMessage =
    {
      val seq = fields.toVector map { case (k, v0) =>
        val h =
          if (k == "*") 0
          else MurmurHash.hash(k)
        val v = v0 match {
          case x: BObject => x.toMessage(fieldNames)
          case x: BArray  => x.toMessageArray(fieldNames)
          case x          => x
        }
        val w = v.wireType.wireTypeByte
        val idx = (h << 8) | w
        (idx, k, v)
      }
      seq foreach { case (idx, k, v) =>
        if (k != "*") {
          fieldNames(idx) = k
        }
      }
      val xs = seq map { case (idx, k, v) => (idx, v) } sortBy {_._1}
      BMessage(xs)
    }
}

/** Represents an Int-indexed message.
 */
case class BMessage(fields: Vector[(Int, BValue)]) extends BValue {
  val wireType: BType = BMessage
  override def toTopMessage: BTopMessage = BTopMessage(this)
  def toBytes: Array[Byte] =
    toBytesWithLength(true)

  private[sjsonnew] def toBytesWithLength(includeLength: Boolean): Array[Byte] =
    {
      val fieldBytes = fields.toVector map {
        case (k, v) => (k, v.toBytes)
      }
      val fieldSize = (0 /: fieldBytes) { _ + _._2.length + 4 }
      val fieldSizeBytes = BUnsignedInt(fieldSize).toBytes
      val size =
        if (includeLength) fieldSize + fieldSizeBytes.length
        else fieldSize
      val buf = ByteBuffer.allocate(size)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      if (includeLength) {
        buf.put(fieldSizeBytes)
      }
      fieldBytes foreach { case (k, v) =>
        // skip field names first
        if (k != BType.lengthFieldNameAsInt) {
          buf.putInt(k)
          buf.put(v)
        }
      }
      fieldBytes foreach { case (k, v) =>
        // skip field names first
        if (k == BType.lengthFieldNameAsInt) {
          buf.putInt(k)
          buf.put(v)
        }
      }
      buf.array
    }
  private[sjsonnew] def toObject(fieldNames: SortedMap[Int, String]): BObject =
    BObject(Map(fields collect { case (idx, v0) if idx != BType.lengthFieldNameAsInt =>
      val k =
        if ((idx >> 8) == 0) "*"
        else fieldNames.getOrElse(idx, (idx >> 8).toString)
      val v = v0 match {
        case x: BMessage => x.toObject(fieldNames)
        case x: BArray   => x.toObjectArray(fieldNames)
        case x           => x
      }
      k -> v
    }: _*))
}

object BMessage extends BType {
  val wireTypeByte: Byte = 0x8
  def parse(buf: ByteBuffer): (BMessage, Int) =
    {
      val (len, inc) = BUnsignedInt.parse(buf)
      val xs = new Array[Byte](len.value)
      buf.get(xs)
      (fromBytes(xs), len.value + inc)
    }
  def fromBytes(ary: Array[Byte]): BMessage = {
    val buf = ByteBuffer.wrap(ary)
    buf.order(ByteOrder.LITTLE_ENDIAN)
    var idx: Int = 0
    val len = ary.length
    val fields = mutable.Map.empty[Int, BValue]
    while (idx < len) {
      val k = buf.getInt
      idx += 4
      val w = BType((k & 0xF).toByte)
      val (x, inc) = w.parse(buf)
      idx += inc
      fields(k) = x
    }
    BMessage(fields.toVector)
  }
}

/** Represents field name for `BMessage` indices.
 */
case class BFieldName(fieldNames: SortedMap[Int, String]) extends BValue {
  val wireType: BType = BFieldName
  def toBytes: Array[Byte] =
    if (fieldNames.isEmpty) Array()
    else {
      val fieldNameBytes = fieldNames.toVector map {
        case (k, v) =>
          (k, BString(v).toBytes)
      }
      val fieldNameSize = (0 /: fieldNameBytes) { _ + _._2.length + 4 }
      val fieldNameSizeSize = BUnsignedInt(fieldNameSize).toBytes
      val size = fieldNameSize + fieldNameSizeSize.length
      val buf = ByteBuffer.allocate(size)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      buf.put(fieldNameSizeSize)
      fieldNameBytes foreach { case (k, v) =>
        buf.putInt(k)
        buf.put(v)
      }
      buf.array
    }
}

object BFieldName extends BType {
  val wireTypeByte: Byte = 0xA
  def parse(buf: ByteBuffer): (BFieldName, Int) =
    {
      val (len, inc) = BUnsignedInt.parse(buf)
      val xs = new Array[Byte](len.value)
      buf.get(xs)
      (parseFieldNames(xs), len.value + inc)
    }

  private[sjsonnew] def parseFieldNames(ary: Array[Byte]): BFieldName =
    {
      val buf = ByteBuffer.wrap(ary)
      buf.order(ByteOrder.LITTLE_ENDIAN)
      var idx: Int = 0
      val len = ary.length
      val fieldNames = mutable.Map.empty[Int, String]
      while (idx < len) {
        val k = buf.getInt
        idx += 4
        val (name, inc) = BString.parse(buf)
        idx += inc
        fieldNames(k) = name.value
      }
      BFieldName(SortedMap(fieldNames.toSeq: _*))
    }
}

case class BTopMessage(message: BMessage) {
  def toBytes: Array[Byte] =
    message.toBytesWithLength(false)
  def toBValue: BValue =
    {
      val fieldNamesOpt = message.fields.find { _._1 == BType.lengthFieldNameAsInt }
      fieldNamesOpt match {
        case None =>
          if (message.fields.size == 1) message.fields.head._2
          else deserializationError(s"Field names were not found in $message")
        case Some((_, BFieldName(fieldNames))) =>
          val obj = message.toObject(fieldNames)
          if ((obj.fields.size == 1) && obj.fields.contains("*")) obj.fields.head._2
          else obj
        case x => deserializationError(s"Unexpeted value $x")
      }
    }
}
object BTopMessage {
  def fromBytes(ary: Array[Byte]): BTopMessage =
    BTopMessage(BMessage.fromBytes(ary))
}

object BUtil {
  def fromHex(hex0: String): Array[Byte] =
    {
      def doFromHex(hex: String): Array[Byte] =
        {
          require((hex.length & 1) == 0, "Hex string must have length 2n.")
          val array = new Array[Byte](hex.length >> 1)
          for (i <- 0 until hex.length by 2) {
            val c1 = hex.charAt(i)
            val c2 = hex.charAt(i + 1)
            array(i >> 1) = ((fromHex(c1) << 4) | fromHex(c2)).asInstanceOf[Byte]
          }
          array
        }
      doFromHex(hex0.replaceAll("\\s", ""))
    }
  private def fromHex(c: Char): Int =
    {
      val b =
        if (c >= '0' && c <= '9')
          (c - '0')
        else if (c >= 'a' && c <= 'f')
          (c - 'a') + 10
        else if (c >= 'A' && c <= 'F')
          (c - 'A') + 10
        else
          throw new RuntimeException("Invalid hex character: '" + c + "'.")
      b
    }

  def toHex(bytes: Array[Byte]): String =
    {
      val buffer = new StringBuilder(bytes.length * 2)
      for (i <- 0 until bytes.length) {
        val b = bytes(i)
        val bi: Int = if (b < 0) b + 256 else b
        buffer append toHex((bi >>> 4).asInstanceOf[Byte])
        buffer append toHex((bi & 0x0F).asInstanceOf[Byte])
      }
      buffer.toString
    }

  private def toHex(b: Byte): Char =
    {
      require(b >= 0 && b <= 15, "Byte " + b + " was not between 0 and 15")
      if (b < 10)
        ('0'.asInstanceOf[Int] + b).asInstanceOf[Char]
      else
        ('A'.asInstanceOf[Int] + (b - 10)).asInstanceOf[Char]
    }

  // Read a single Byte, then read from ByteBuffer while the highest bit is turned on
  // https://developers.google.com/protocol-buffers/docs/encoding#varints
  def readVarIntBytes(buf: ByteBuffer): Array[Byte] =
    {
      val xs = new ArrayBuffer[Byte]
      var x: Byte = buf.get
      xs += x
      while ((x & 0x80) != 0) {
        x = buf.get
        xs += x
      }
      xs.toArray
    }
}
