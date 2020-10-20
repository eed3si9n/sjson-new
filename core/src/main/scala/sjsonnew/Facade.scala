/*
 * Original implementation (C) 2012-2016 Erik Osheim
 * Adapted and extended in 2016 by Eugene Yokota
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package sjsonnew

// https://github.com/typelevel/jawn/blob/5f03ff5d9bccb483f6fd87d8e79cdb697f8794b6/parser/src/main/scala/jawn/Facade.scala

/**
 * BuilderFacade is a type class that describes how JSON AST elements of
 * type J can be constructed.
 *
 * BuilderFacade[J] also uses FContext[J] instances, so implementors will
 * usually want to define both.
 */
trait BuilderFacade[J] {
  def singleContext(): FContext[J]
  def arrayContext(): FContext[J]
  def objectContext(): FContext[J]

  def jnull(): J
  def jfalse(): J
  def jtrue(): J
  def jdouble(d: Double): J
  def jnumstring(s: String): J
  def jbigdecimal(d: BigDecimal): J
  def jintstring(s: String): J
  def jint(i: Int): J
  def jlong(l: Long): J
  def jstring(s: String): J
}


/**
 * FContext is used to construct nested JSON values.
 *
 * The most common cases are to build objects and arrays. However,
 * this type is also used to build a single top-level JSON element, in
 * cases where the entire JSON document consists of "333.33".
 */
trait FContext[J] {
  def addField(s: String): Unit
  def add(v: J): Unit
  def finish: J
  def isObj: Boolean
}

/**
 * ExtractorFacade is a type class that describes how JSON AST elements of
 * type J can be extracted.
 */
trait ExtractorFacade[J] {
  def isJnull(value: J): Boolean
  def isObject(value: J): Boolean
  def extractInt(value: J): Int
  def extractLong(value: J): Long
  def extractFloat(value: J): Float
  def extractDouble(value: J): Double
  def extractBigDecimal(value: J): BigDecimal
  def extractBoolean(value: J): Boolean
  def extractString(value: J): String
  def extractArray(value: J): Vector[J]
  def extractObject(value: J): (Map[String, J], Vector[String])
}

/**
 * Facade is a type class that describes how JSON AST elements of
 * type J can be constructed, and how value can be extracted.
 *
 * Facade[J] also uses FContext[J] instances, so implementors will
 * usually want to define both.
 */
trait Facade[J] extends BuilderFacade[J] with ExtractorFacade[J]
