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
package support.murmurhash

import org.scalatest.FlatSpec
import BUtil._
import LList._

class MurmurhashSpec extends FlatSpec with BasicJsonProtocol {
  "The IntJsonFormat" should "convert an Int to an int hash" in {
    assert(Hasher.hashUnsafe[Int](1) === 1527037976)
  }

  "The LongJsonFormat" should "convert a Long to an int hash" in {
    assert(Hasher.hashUnsafe[Long](0x200000000L) === -1422712199)
  }

  "The FloatJsonFormat" should "convert a Float to an int hash" in {
    assert(Hasher.hashUnsafe[Float](4.2f) === 1357889325)
  }
  it should "convert a Float.NaN to an int hash" in {
    assert(Hasher.hashUnsafe[Float](Float.NaN) === -813195245)
  }

  "The BigIntJsonFormat" should "convert a BigInt to an int hash" in {
    assert(Hasher.hashUnsafe(BigInt(42)) === bigDecimalMessage)
  }

  "The BigDecimalJsonFormat" should "convert a BigDecimal to an int hash" in {
    assert(Hasher.hashUnsafe(BigDecimal(42)) === bigDecimalMessage)
  }

  val bigDecimalMessage = 2083694762

  "The UnitJsonFormat" should "convert Unit to an int hash" in {
    assert(Hasher.hashUnsafe(()) === oneMessage)
  }

  lazy val oneMessage = 1527037976
  "The BooleanJsonFormat" should "convert true to an int hash" in {
    assert(Hasher.hashUnsafe(true) === trueMessage)
  }
  it should "convert false to an int hash 0xc2" in {
    assert(Hasher.hashUnsafe(false) === falseMessage)
  }
  lazy val trueMessage = 0xc3
  lazy val falseMessage = 0xc2

  "The StringJsonFormat" should "convert a String to a length delimited string" in {
    assert(Hasher.hashUnsafe("Hello") === stringHash)
  }
  lazy val stringHash = 1509011998

  "The optionFormat" should "convert None to the nil hash" in {
    assert(Hasher.hashUnsafe(None.asInstanceOf[Option[Int]]) === nullHash)
  }
  it should "convert Some(Hello) to an int hash" in {
    assert(Hasher.hashUnsafe(Some("Hello").asInstanceOf[Option[String]]) === stringHash)
  }
  lazy val nullHash = 0xc0

  val map = Map("a" -> 1, "b" -> 2)
  "The mapFormat" should "convert a Map[String, Int] to an int hash" in {
    if (scala.util.Properties.versionNumberString.startsWith("2.13.")) {
      assert(Hasher.hashUnsafe(map) === -875559656)
    } else {
      assert(Hasher.hashUnsafe(map) === mapHash)
    }
  }
  lazy val mapHash = -929861022

  "The listFormat" should "convert a List[Int] to a length delimited list" in {
    if (scala.util.Properties.versionNumberString.startsWith("2.13.")) {
      assert(Hasher.hashUnsafe(list) === 836729159)
    } else {
      assert(Hasher.hashUnsafe(list) === listHash)
    }
  }
  it should "convert a List[Map[String, Int]] to a length delimited list" in {
    if (scala.util.Properties.versionNumberString.startsWith("2.13.")) {
      assert(Hasher.hashUnsafe(complexList) === 527995371)
    } else {
      assert(Hasher.hashUnsafe(complexList) === complexListHash)
    }
  }
  lazy val list = List(1, 2)
  lazy val complexList = List(map)
  lazy val listHash = -528206911
  lazy val complexListHash = 1388776897

  "The llistFormat" should "convert an empty list to an int hash" in {
    assert(Hasher.hashUnsafe(emptyList) === emptyHash)
  }
  it should "convert a list to an int hash" in {
    if (scala.util.Properties.versionNumberString.startsWith("2.13.")) {
      assert(Hasher.hashUnsafe(a1) === -1240310382)
    } else {
      assert(Hasher.hashUnsafe(a1) === a1Hash)
    }
  }
  lazy val emptyList = LNil
  lazy val emptyHash = -1609326920
  lazy val a1 = ("a", 1) :*: LNil
  lazy val ba1 = ("b", a1) :*: LNil
  lazy val a1Hash = 1371594665
}
