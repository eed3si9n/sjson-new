package sjsonnew
package support.scalajson.unsafe

import shaded.scalajson.ast.unsafe._
import scala.collection.mutable.ArrayBuffer
import shaded.org.typelevel.jawn.{ Facade, FContext, SupportParser }

object Parser extends SupportParser[JValue] {
  implicit val facade: Facade[JValue] =
    new Facade[JValue] {
      def jnull(index: Int) = JNull
      def jfalse(index: Int) = JFalse
      def jtrue(index: Int) = JTrue
      def jnum(s: CharSequence, decIndex: Int, expIndex: Int, index: Int) = JNumber(s.toString)
      def jint(s: String) = JNumber(s)
      def jstring(s: CharSequence, index: Int) = JString(s.toString)
      def singleContext(index: Int) = new FContext[JValue] {
        var value: JValue = _
        def add(s: CharSequence, index: Int) = value = jstring(s, index)
        def add(v: JValue, index: Int) = value = v
        def finish(index: Int): JValue = value
        def isObj: Boolean = false
      }
      def arrayContext(index: Int) = new FContext[JValue] {
        private val vs = ArrayBuffer.empty[JValue]
        def add(s: CharSequence, index: Int) = vs += jstring(s, index)
        def add(v: JValue, index: Int) = vs += v
        def finish(index: Int): JValue = JArray(vs.toArray)
        def isObj: Boolean = false
      }
      def objectContext(index: Int) = new FContext[JValue] {
        private var key: String = _
        private var vs = ArrayBuffer.empty[JField]
        private def andNullKey[A](t: => Unit): Unit = { t; key = null }
        def add(s: CharSequence, index: Int) = {
          if (key == null) key = s.toString
          else andNullKey(vs += JField(key, jstring(s, index)))
        }
        def add(v: JValue, index: Int) = andNullKey(vs += JField(key, v))
        def finish(index: Int): JValue = JObject(vs.toArray)
        def isObj: Boolean = true
      }
    }
}
