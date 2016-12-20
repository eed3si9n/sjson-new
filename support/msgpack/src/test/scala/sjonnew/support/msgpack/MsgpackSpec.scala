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

import org.scalatest.FlatSpec
import BUtil._
import LList._

class MsgpackSpec extends FlatSpec with BasicJsonProtocol {
  "The IntJsonFormat" should "convert an Int to an int message" in {
    assert(Converter.toBinaryUnsafe[Int](150) === intMessage)
  }
  it should "convert the binary message back to Int" in {
    assert(Converter.fromBinaryUnsafe[Int](intMessage) === 150)
  }
  // https://github.com/msgpack/msgpack/blob/master/spec.md
  lazy val intMessage = fromHex("CC 96")

  "The LongJsonFormat" should "convert a Long to an int message" in {
    assert(Converter.toBinaryUnsafe[Long](0x200000000L) === longMessage)
  }
  it should "convert the binary message back to Long" in {
    assert(Converter.fromBinaryUnsafe[Long](longMessage) === 0x200000000L)
  }
  // https://github.com/msgpack/msgpack/blob/master/spec.md
  lazy val longMessage = fromHex("CF 00 00 00 02 00 00 00 00")

  "The FloatJsonFormat" should "convert a Float to a fixed double message" in {
    assert(Converter.toBinaryUnsafe[Float](4.2f) === floatMessage)
  }
  it should "convert the binary message back to Float" in {
    assert(Converter.fromBinaryUnsafe[Float](floatMessage) === 4.2f)
  }
  it should "convert a Float.NaN to a NaN messge" in {
    assert(Converter.toBinaryUnsafe[Float](Float.NaN) === nanMessage)
  }
  // https://github.com/msgpack/msgpack/blob/master/spec.md
  lazy val floatMessage = fromHex("CB 4010CCCCC0000000 ")
  lazy val nanMessage = fromHex("CB 7FF8000000000000")

  "The BigIntJsonFormat" should "convert a BigInt to a length delimited string" in {
    assert(Converter.toBinaryUnsafe(BigInt(42)) === bigDecimalMessage)
  }
  it should "convert the binary message back a BigInt" in {
    assert(Converter.fromBinaryUnsafe[BigInt](bigDecimalMessage) === BigInt(42))
  }
  "The BigDecimalJsonFormat" should "convert a BigDecimal to a length delimited string" in {
    assert(Converter.toBinaryUnsafe(BigDecimal(42)) === bigDecimalMessage)
  }
  it should "convert the binary message back a BigDecimal" in {
    assert(Converter.fromBinaryUnsafe[BigDecimal](bigDecimalMessage) === BigDecimal(42))
  }
  // https://github.com/msgpack/msgpack/blob/master/spec.md#str-format-family
  // UTF-8 for "42" is 0x3432
  lazy val bigDecimalMessage = fromHex("A2 3432")

  "The UnitJsonFormat" should "convert Unit to an int message" in {
    assert(Converter.toBinaryUnsafe(()) === oneMessage)
  }
  it should "convert the binary message back the Unit" in {
    assert(Converter.fromBinaryUnsafe[Unit](oneMessage) === ((): Unit))
  }
  lazy val oneMessage = fromHex("01")

  "The BooleanJsonFormat" should "convert true to true value" in {
    assert(Converter.toBinaryUnsafe(true) === trueMessage)
  }
  it should "convert false to a varint message 0" in {
    assert(Converter.toBinaryUnsafe(false) === falseMessage)
  }
  it should "convert from 1 back to true" in {
    assert(Converter.fromBinaryUnsafe[Boolean](trueMessage) === true)
  }
  it should "convert from 0 back to false" in {
    assert(Converter.fromBinaryUnsafe[Boolean](falseMessage) === false)
  }
  // https://github.com/msgpack/msgpack/blob/master/spec.md#bool-format-family
  lazy val trueMessage = fromHex("C3")
  lazy val falseMessage = fromHex("C2")

  "The StringJsonFormat" should "convert a String to a length delimited string" in {
    assert(Converter.toBinaryUnsafe("Hello") === stringMessage)
  }
  it should "convert the binary message back to a String" in {
    assert(Converter.fromBinaryUnsafe[String](stringMessage) === "Hello")
  }
  // UTF-8 for "Hello" is 0x48656C6C6F
  lazy val stringMessage = fromHex("A5 48656C6C6F")

  "The optionFormat" should "convert None to the nil message" in {
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
  // https://github.com/msgpack/msgpack/blob/master/spec.md#nil-format
  lazy val nullMessage = fromHex("C0")

  val map = Map("a" -> 1, "b" -> 2)
  "The mapFormat" should "convert a Map[String, Int] to a binary message" in {
    assert(Converter.toBinaryUnsafe(map) === mapMessage)
  }
  it should "convert the binary message back to a Map[String, Int]" in {
    assert(Converter.fromBinaryUnsafe[Map[String, Int]](mapMessage) === map)
  }
  // https://github.com/msgpack/msgpack/blob/master/spec.md#map-format-family
  lazy val mapMessage = fromHex(
    "82 " +    // Fix map for 2 entries
    "A1 61 " + // string of length 1, "a"
    "01" +     // 1
    "A1 62 " + // string of length 1, "b"
    "02"       // 2
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
  // https://github.com/msgpack/msgpack/blob/master/spec.md#array-format-family
  lazy val listMessage = fromHex("92 01 02")
  lazy val complexListMessage = fromHex(
    "91 " + // array with single item
    "82 A1 61 01 A1 62 02"
    )

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
  lazy val emptyMessage = fromHex("80")
  lazy val a1 = ("a", 1) :*: LNil
  lazy val ba1 = ("b", a1) :*: LNil
  lazy val a1Message = fromHex(
    "82 " +    // Fix map for 2 entry
    "A7 24 66 69 65 6C 64 73 " + // string of length 7, "$fields"
    "91 " + // array with single item
    "A1 61 " + // string of length 1, "a" (0x61)
    "A1 61 " + // string of length 1, "a" (0x61)
    "01"       // 1
    )
  lazy val ba1Message = fromHex(
    "82 " +    // Fix map for 1 entry
    "A7 24 66 69 65 6C 64 73 " + // string of length 7, "$fields"
    "91 " + // array with single item
    "A1 62 " + // string of length 1, "b" (0x62)
    "A1 62 " + // string of length 1, "b" (0x62)
    "82 " +    // Fix map for 1 entry
    "A7 24 66 69 65 6C 64 73 " + // string of length 7, "$fields"
    "91 " + // array with single item
    "A1 61" +  // string of length 1, "a" (0x61)
    "A1 61" +  // string of length 1, "a" (0x61)
    "01"       // 1
    )
}
