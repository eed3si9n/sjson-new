package sjsonnew
package support.json4s

import scala.collection.mutable
import org.json4s.JsonAST._

object Converter extends SupportConverter[JValue] {
  implicit val facade: Facade[JValue] = FacadeImpl
  private object FacadeImpl extends Facade[JValue] {
    def jnull() = JNull
    def jfalse() = JBool(false)
    def jtrue() = JBool(true)
    def jnumstring(s: String) = JDouble(java.lang.Double.parseDouble(s))
    def jintstring(s: String) = JInt(BigInt(s))
    def jint(i: Int) = JInt(BigInt(i))
    def jlong(l: Long) = JInt(BigInt(l))
    def jdouble(d: Double) = JDouble(d)
    def jbigdecimal(d: BigDecimal) = JDecimal(d)
    def jstring(s: String) = JString(s)

    def singleContext() =
      new FContext[JValue] {
        var value: JValue = null
        def add(s: String) { value = jstring(s) }
        def add(v: JValue) { value = v }
        def finish: JValue = value
        def isObj: Boolean = false
      }

    def arrayContext() =
      new FContext[JValue] {
        val vs = mutable.ListBuffer.empty[JValue]
        def add(s: String) { vs += jstring(s) }
        def add(v: JValue) { vs += v }
        def finish: JValue = JArray(vs.toList)
        def isObj: Boolean = false
      }

    def objectContext() =
      new FContext[JValue] {
        var key: String = null
        var vs = List.empty[JField]
        def add(s: String): Unit =
          if (key == null) key = s
          else { vs = JField(key, jstring(s)) :: vs; key = null }
        def add(v: JValue): Unit =
          { vs = JField(key, v) :: vs; key = null }
        def finish: JValue = JObject(vs)
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
        case JDouble(x) => x.toInt
        case JDecimal(x) => x.toInt
        case JInt(x) => x.toInt
        case x => deserializationError("Expected Int as JNumber, but got " + x)
      }
    def extractLong(value: JValue): Long =
      value match {
        case JDouble(x) => x.toLong
        case JDecimal(x) => x.toLong
        case JInt(x) => x.toLong
        case x => deserializationError("Expected Long as JNumber, but got " + x)
      }
    def extractFloat(value: JValue): Float =
      value match {
        case JDouble(x) => x.toFloat
        case JDecimal(x) => x.toFloat
        case JInt(x) => x.toFloat
        case JNull      => Float.NaN
        case x => deserializationError("Expected Float as JNumber, but got " + x)
      }
    def extractDouble(value: JValue): Double =
      value match {
        case JDouble(x) => x.toDouble
        case JDecimal(x) => x.toDouble
        case JInt(x) => x.toDouble
        case JNull      => Double.NaN
        case x => deserializationError("Expected Double as JNumber, but got " + x)
      }
    def extractBigDecimal(value: JValue): BigDecimal =
      value match {
        case JDouble(x) => BigDecimal(x)
        case JDecimal(x) => x
        case JInt(x) => BigDecimal(x)
        case x => deserializationError("Expected BigDecimal as JNumber, but got " + x)
      }
    def extractBoolean(value: JValue): Boolean =
      value match {
        case JBool(x) => x
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
        case x => deserializationError("Expected List as JArray, but got " + x)
      }
    def extractObject(value: JValue): Vector[(String, JValue)] =
      value match {
        case JObject(fields) => fields.toVector
        case x => deserializationError("Expected Map as JsObject, but got " + x)
      }
  }
}
