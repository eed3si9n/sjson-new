/*
 * Original implementation (C) 2012-2016 Erik Osheim
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

import scala.collection.mutable

/**
 * Facade is a type class that describes how Jawn should construct
 * JSON AST elements of type J.
 *
 * Facade[J] also uses FContext[J] instances, so implementors will
 * usually want to define both.
 */
trait SimpleFacade[J] extends Facade[J] {
  def jarray(vs: List[J]): J
  def jobject(vs: Map[String, J]): J

  def singleContext() = new FContext[J] {
    var value: J = _
    def addField(s: String): Unit = { value = jstring(s) }
    def add(v: J) { value = v }
    def finish: J = value
    def isObj: Boolean = false
  }

  def arrayContext() = new FContext[J] {
    val vs = mutable.ListBuffer.empty[J]
    def addField(s: String): Unit = { vs += jstring(s) }
    def add(v: J) { vs += v }
    def finish: J = jarray(vs.toList)
    def isObj: Boolean = false
  }

  def objectContext() = new FContext[J] {
    var key: String = null
    var vs = Map.empty[String, J]
    def addField(s: String): Unit =
      if (key == null) { key = s }
      else { vs = vs.updated(key, jstring(s)); key = null }
    def add(v: J): Unit =
      { vs = vs.updated(key, v); key = null }
    def finish = jobject(vs)
    def isObj = true
  }
}
