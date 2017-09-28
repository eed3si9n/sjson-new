package sjsonnew
package support.scalajson.unsafe

import scala.collection.mutable
import shaded.scalajson.ast.unsafe._

object Converter extends SupportConverter[JValue] {
  implicit val facade: Facade[JValue] = FacadeImpl
  private object FacadeImpl extends Facade[JValue] {
    def jnull() = JNull
    def jfalse() = JFalse
    def jtrue() = JTrue
    def jnumstring(s: String) = JNumber(s)
    def jintstring(s: String) = JNumber(s)
    def jint(i: Int) = JNumber(i)
    def jlong(l: Long) = JNumber(l)
    def jdouble(d: Double) = JNumber(d)
    def jbigdecimal(d: BigDecimal) = JNumber(d)
    def jstring(s: String) = JString(s)

    def singleContext() =
      new FContext[JValue] {
        var value: JValue = null
        def addField(s: String): Unit = { value = jstring(s) }
        def add(v: JValue) { value = v }
        def finish: JValue = value
        def isObj: Boolean = false
      }

    def arrayContext() =
      new FContext[JValue] {
        val vs = mutable.ArrayBuffer.empty[JValue]
        def addField(s: String): Unit = { vs += jstring(s) }
        def add(v: JValue) { vs += v }
        def finish: JValue = JArray(vs.toArray)
        def isObj: Boolean = false
      }

    def objectContext() =
      new FContext[JValue] {
        var key: String = null
       val vs = mutable.ArrayBuffer.empty[JField]
        def addField(s: String): Unit =
          if (key == null) key = s
          else { vs += JField(key, jstring(s)); key = null }
        def add(v: JValue): Unit =
          { vs += JField(key, v); key = null }
        def finish: JValue = JObject(vs.toArray)
        def isObj: Boolean = true
      }

    def isJnull(value: JValue): Boolean =
      value match {
        case JNull => true
        case _      => false
      }
    def isObject(value: JValue): Boolean =
      value match {
        case JObject(_) => true
        case _          => false
      }
    def extractInt(value: JValue): Int =
      value match {
        case x: JNumber => x.value.toInt
        case x => deserializationError("Expected Int as JNumber, but got " + x)
      }
    def extractLong(value: JValue): Long =
      value match {
        case x: JNumber => x.value.toLong
        case x => deserializationError("Expected Long as JNumber, but got " + x)
      }
    def extractFloat(value: JValue): Float =
      value match {
        case x: JNumber => x.value.toFloat
        case JNull      => Float.NaN
        case x => deserializationError("Expected Float as JNumber, but got " + x)
      }
    def extractDouble(value: JValue): Double =
      value match {
        case x: JNumber => x.value.toDouble
        case JNull      => Double.NaN
        case x => deserializationError("Expected Double as JNumber, but got " + x)
      }
    def extractBigDecimal(value: JValue): BigDecimal =
      value match {
        case x: JNumber => BigDecimal(x.value)
        case x => deserializationError("Expected BigDecimal as JNumber, but got " + x)
      }
    def extractBoolean(value: JValue): Boolean =
      value match {
        case JFalse => false
        case JTrue  => true
        case x => deserializationError("Expected JBool, but got " + x)
      }
    def extractString(value: JValue): String =
      value match {
        case JString(x) => x
        case x => deserializationError("Expected String as JString, but got " + x)
      }
    def extractArray(value: JValue): Vector[JValue] =
      value match {
        case JArray(elements) => elements.toVector
        case JNull            => Vector.empty
        case x => deserializationError("Expected List as JArray, but got " + x)
      }
    def extractObject(value: JValue): (Map[String, JValue], Vector[String]) =
      value match {
        case JObject(fs) =>
          val names = (fs map { case JField(k, v) => k }).toVector
          val fields = Map((fs map { case JField(k, v) => (k, v) }): _*)
          (fields, names)
        case JNull => (Map.empty, Vector.empty)
        case x => deserializationError("Expected Map as JsObject, but got " + x)
      }
  }
}
