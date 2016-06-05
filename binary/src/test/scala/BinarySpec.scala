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

import org.scalatest.FlatSpec
import BUtil._
import LList._

class BinarySpec extends FlatSpec with BasicJsonProtocol {
  "The IntJsonFormat" should "convert an Int to a varint message" in {
    assert(Converter.toBinaryUnsafe[Int](150) === intMessage)
  }
  it should "convert the binary message back to Int" in {
    assert(Converter.fromBinaryUnsafe[Int](intMessage) === 150)
  }
  // https://developers.google.com/protocol-buffers/docs/encoding#varints
  lazy val intMessage = fromHex("01 00 00 00 AC 02")

  "The LongJsonFormat" should "convert a Long to a varint message" in {
    assert(Converter.toBinaryUnsafe[Long](150L) === longMessage)
  }
  it should "convert the binary message back to Long" in {
    assert(Converter.fromBinaryUnsafe[Long](longMessage) === 150L)
  }
  lazy val longMessage = fromHex("03 00 00 00 AC 02")

  "The ByteJsonFormat" should "convert a Byte to a varint message" in {
    assert(Converter.toBinaryUnsafe(42.toByte) === signedIntMessage)
  }
  it should "convert the binary message back a Byte" in {
    assert(Converter.fromBinaryUnsafe[Byte](signedIntMessage) === 42.toByte)
  }
  lazy val signedIntMessage = fromHex("01 00 00 00 54")

  "The ShortJsonFormat" should "convert a Short to a varint message" in {
    assert(Converter.toBinaryUnsafe(42.toShort) === signedIntMessage)
  }
  it should "convert the binary message back a Short" in {
    assert(Converter.fromBinaryUnsafe[Short](signedIntMessage) === 42.toShort)
  }

  "The FloatJsonFormat" should "convert a Float to a fixed double message" in {
    assert(Converter.toBinaryUnsafe[Float](4.2f) === floatMessage)
  }
  it should "convert the binary message back to Float" in {
    assert(Converter.fromBinaryUnsafe[Float](floatMessage) === 4.2f)
  }
  it should "convert a Float.NaN to a null messge" in {
    assert(Converter.toBinaryUnsafe[Float](Float.NaN) === nullMessage)
  }
  lazy val floatMessage = fromHex("04 00 00 00 000000C0CCCC1040")

  "The DoubleJsonFormat" should "convert a Double to a fixed double message" in {
    assert(Converter.toBinaryUnsafe[Double](4.2) === doubleMessage)
  }
  it should "convert the binary message back to Double" in {
    assert(Converter.fromBinaryUnsafe[Double](doubleMessage) === 4.2)
  }
  it should "convert a Double.NaN to a JsNull" in {
    assert(Converter.toBinaryUnsafe[Double](Double.NaN) === nullMessage)
  }
  lazy val doubleMessage = fromHex("04 00 00 00 CDCCCCCCCCCC1040")

  "BFixedInt" should "represent Int in a fixed Int32 message" in {
    assert(BFixedInt(150).toTopMessage.toBytes === fixedIntMessage)
  }
  it should "convert the binary message back to Int" in {
    assert(Converter.fromBinaryUnsafe[Int](fixedIntMessage) === 150)
  }
  // 150 in binary is 0x00000096
  lazy val fixedIntMessage = fromHex("05 00 00 00 96 00 00 00")

  "BFixedLong" should "represent Long in a fixed Int64 message" in {
    assert(BFixedLong(150L).toTopMessage.toBytes === fixedLongMessage)
  }
  it should "convert the binary message back to Long" in {
    assert(Converter.fromBinaryUnsafe[Long](fixedLongMessage) === 150L)
  }
  // 150 in binary is 0x00000096
  lazy val fixedLongMessage = fromHex("06 00 00 00 96 00 00 00 00 00 00 00")

  "The BigDecimalJsonFormat" should "convert a BigDecimal to a length delimited string" in {
    assert(Converter.toBinaryUnsafe(BigDecimal(42)) === bigDecimalMessage)
  }
  it should "convert the binary message back a BigDecimal" in {
    assert(Converter.fromBinaryUnsafe[BigDecimal](bigDecimalMessage) === BigDecimal(42))
  }
  // UTF-8 for "42" is 0x3432
  lazy val bigDecimalMessage = fromHex("0B 00 00 00 02 3432")

  "The BigIntJsonFormat" should "convert a BigInt to a length delimited string" in {
    assert(Converter.toBinaryUnsafe(BigInt(42)) === bigDecimalMessage)
  }
  it should "convert the binary message back a BigInt" in {
    assert(Converter.fromBinaryUnsafe[BigInt](bigDecimalMessage) === BigInt(42))
  }

  "The UnitJsonFormat" should "convert Unit to a varint message" in {
    assert(Converter.toBinaryUnsafe(()) === oneMessage)
  }
  it should "convert the binary message back the Unit" in {
    assert(Converter.fromBinaryUnsafe[Unit](oneMessage) === ((): Unit))
  }
  lazy val oneMessage = fromHex("01 00 00 00 02")
  lazy val zeroMessage = fromHex("01 00 00 00 00")

  "The BooleanJsonFormat" should "convert true to a varint message 1" in {
    assert(Converter.toBinaryUnsafe(true) === oneMessage)
  }
  it should "convert false to a varint message 0" in {
    assert(Converter.toBinaryUnsafe(false) === zeroMessage)
  }
  it should "convert from 1 back to true" in {
    assert(Converter.fromBinaryUnsafe[Boolean](oneMessage) === true)
  }
  it should "convert from 0 back to false" in {
    assert(Converter.fromBinaryUnsafe[Boolean](zeroMessage) === false)
  }

  "The CharJsonFormat" should "convert a Char to a length delimited string" in {
    assert(Converter.toBinaryUnsafe('c') === cMessage)
  }
  it should "convert the binary message to a Char" in {
    assert(Converter.fromBinaryUnsafe[Char](cMessage) === 'c')
  }
  lazy val cMessage = fromHex("07 00 00 00 01 63")

  "The StringJsonFormat" should "convert a String to a length delimited string" in {
    assert(Converter.toBinaryUnsafe("Hello") === stringMessage)
  }
  it should "convert the binary message back to a String" in {
    assert(Converter.fromBinaryUnsafe[String](stringMessage) === "Hello")
  }
  // UTF-8 for "Hello" is 0x48656C6C6F
  lazy val stringMessage = fromHex("07 00 00 00 05 48656C6C6F")

  val map = Map("a" -> 1, "b" -> 2)
  "The mapFormat" should "convert a Map[String, Int] to a binary message" in {
    assert(Converter.toBinaryUnsafe(map) === mapMessage)
  }
  it should "convert the binary message back to a Map[String, Int]" in {
    assert(Converter.fromBinaryUnsafe[Map[String, Int]](mapMessage) === map)
  }
  lazy val mapMessage = fromHex(
    "01 96 44 87 " + // type varint 1, reverse of hash of "a" (0x2A 87 44 96)
    "02 " + // zigzag encoding of 1
    "01 41 F9 E8 " + // type varint 1, reverse of hash of "b" (0x86 E8 F9 41)
    "04 " + // zigzag encoding of 2
    "0A 00 00 00 " + // singluar field names
    "0C " + // length 12 bytes
    "01 96 44 87 " + // idx for "a"
    "01 61 " + // length 1, then UTF-8 for "a" (0x61)
    "01 41 F9 E8 " + // idx for "b"
    "01 62" // length 1, then UTF-8 for "b" (0x62)
    )

  "The listFormat" should "convert a List[Int] to a length delimited list" in {
    assert(Converter.toBinaryUnsafe(list) === listMessage)
  }
  it should "convert the binary message back to List[Int]" in {
    assert(Converter.fromBinaryUnsafe[List[Int]](listMessage) === list)
  }
  it should "convert a List[Map[String, Int]] to a length delimited list" in {
    assert(Converter.toBinaryUnsafe(complexList) === complexListMessage)
  }
  it should "convert the binary message back to List[Map[String, Int]]" in {
    assert(Converter.fromBinaryUnsafe[List[Map[String, Int]]](complexListMessage) === complexList)
  }
  lazy val list = List(1, 2)
  lazy val complexList = List(map)
  lazy val listMessage = fromHex("09 00 00 00 0A 11 00 00 00 02 21 00 00 00 04")
  lazy val complexListMessage = fromHex(
    "09 00 00 00 " + // singular list
    "0F " + // length 15 bytes
    "18 00 00 00 " + // index 1, type embedded message (8)
    "0A" + // length 10 bytes for the embedded message
    "01 96 44 87 02 01 41 F9 E8 04 " +
    "0A 00 00 00 0C 01 96 44 87 01 61 01 41 F9 E8 01 62"
    )

  "The optionFormat" should "convert None to BNull message" in {
    assert(Converter.toBinaryUnsafe(None.asInstanceOf[Option[Int]]) === nullMessage)
  }
  it should "convert the message back to None" in {
    assert(Converter.fromBinaryUnsafe[Option[Int]](nullMessage) === None)
  }
  it should "convert Some(Hello) to a length delimited string message" in {
    assert(Converter.toBinaryUnsafe(Some("Hello").asInstanceOf[Option[String]]) === stringMessage)
  }
  it should "convert the message back to Some(Hello)" in {
    assert(Converter.fromBinaryUnsafe[Option[String]](stringMessage) === Some("Hello"))
  }

  lazy val nullMessage = fromHex("00 00 00 00 00")

  "The llistFormat" should "convert an empty list to an empty message" in {
    assert(Converter.toBinaryUnsafe(emptyList) === emptyMessage)
  }
  it should "convert a list to a binary message" in {
    assert(Converter.toBinaryUnsafe(a1) === a1Message)
  }
  it should "convert from the binary message back to the simple list" in {
    assert(Converter.fromBinaryUnsafe[Int :*: LNil](a1Message) === a1)
  }
  it should "convert a nested list to a binary message" in {
    assert(Converter.toBinaryUnsafe(ba1) === ba1Message)
  }
  it should "convert the binary message back to the nested list" in {
    assert(Converter.fromBinaryUnsafe[(Int :*: LNil) :*: LNil](ba1Message) === ba1)
  }
  lazy val emptyList = LNil
  lazy val emptyMessage = fromHex("")
  lazy val a1 = ("a", 1) :*: LNil
  lazy val ba1 = ("b", a1) :*: LNil
  // Hash of "a" is 0x2A 87 44 96
  // Hash of "b" is 0x86 E8 F9 41
  // UTF-8 for "a" is 0x61
  // UTF-8 for "b" is 0x62
  lazy val a1Message = fromHex("01 96 44 87 02 " +
    "0A 00 00 00 06 01 96 44 87 01 61")
  lazy val ba1Message = fromHex("08 41 F9 E8 05 01 96 44 87 02 " +
    "0A 00 00 00 0C 01 96 44 87 01 61 08 41 F9 E8 01 62")
}
