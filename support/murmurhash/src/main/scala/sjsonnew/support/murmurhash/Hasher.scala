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

import scala.util.Try
import HashUtil.hashLong
import java.lang.{ Double => JDouble }
import scala.util.hashing.MurmurHash3

object Hasher extends SupportHasher[Int] {
  private val nullHash = 0xc0
  private val falseHash = 0xc2
  private val trueHash = 0xc3

  implicit val facade: BuilderFacade[Int] = FacadeImpl
  private object FacadeImpl extends SimpleBuilderFacade[Int] {
    val jnull                 = nullHash
    val jfalse                = falseHash
    val jtrue                 = trueHash
    def jnumstring(s: String) = jstring(s)
    def jintstring(s: String) = jstring(s)
    def jint(i: Int)          = hashLong(i.toLong)
    def jlong(l: Long)        = hashLong(l)
    def jdouble(d: Double)    = hashLong(JDouble.doubleToRawLongBits(d))
    def jbigdecimal(d: BigDecimal) = jstring(d.toString)
    def jstring(s: String)    = MurmurHash3.stringHash(s)
    def jarray(vs: List[Int]): Int = MurmurHash3.seqHash(vs)
    def jobject(vs: Map[String, Int]): Int = MurmurHash3.mapHash(vs)
  }
}
