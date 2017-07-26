package sjsonnew
package support.spray

import spray.json.{ deserializationError => _, _ }

object Converter extends SupportConverter[JsValue] {
  val facade: Facade[JsValue] = FacadeImpl
  private object FacadeImpl extends SimpleFacade[JsValue] {
    def jnull() = JsNull
    def jfalse() = JsFalse
    def jtrue() = JsTrue
    def jdouble(d: Double) = JsNumber(d)
    def jnumstring(s: String) = JsNumber(s)
    def jbigdecimal(d: BigDecimal) = JsNumber(d)
    def jintstring(s: String) = JsNumber(s)
    def jint(i: Int) = JsNumber(i)
    def jlong(l: Long) = JsNumber(l)
    def jstring(s: String) = JsString(s)
    def jarray(vs: List[JsValue]) = JsArray(vs: _*)
    def jobject(vs: Map[String, JsValue]) = JsObject(vs)
    def isJnull(value: JsValue): Boolean =
      value match {
        case JsNull => true
        case _      => false
      }

    def isObject(value: JsValue): Boolean =
      value match {
        case JsObject(_) => true
        case _           => false
      }
    def extractInt(value: JsValue): Int =
      value match {
        case JsNumber(x) => x.intValue
        case x => deserializationError("Expected Int as JsNumber, but got " + x)
      }
    def extractLong(value: JsValue): Long =
      value match {
        case JsNumber(x) => x.longValue
        case x => deserializationError("Expected Long as JsNumber, but got " + x)
      }
    def extractFloat(value: JsValue): Float =
      value match {
        case JsNumber(x) => x.floatValue
        case JsNull      => Float.NaN
        case x => deserializationError("Expected Float as JsNumber, but got " + x)
      }
    def extractDouble(value: JsValue): Double =
      value match {
        case JsNumber(x) => x.doubleValue
        case JsNull      => Double.NaN
        case x => deserializationError("Expected Double as JsNumber, but got " + x)
      }
    def extractBigDecimal(value: JsValue): BigDecimal =
      value match {
        case JsNumber(x) => x
        case x => deserializationError("Expected BigDecimal as JsNumber, but got " + x)
      }
    def extractBoolean(value: JsValue): Boolean =
      value match {
        case JsTrue => true
        case JsFalse => false
        case x => deserializationError("Expected JsBoolean, but got " + x)
      }
    def extractString(value: JsValue): String =
      value match {
        case JsString(x) => x
        case x => deserializationError("Expected String as JsString, but got " + x)
      }
    def extractArray(value: JsValue): Vector[JsValue] =
      value match {
        case JsArray(elements) => elements
        case JsNull            => Vector.empty
        case x => deserializationError("Expected List as JsArray, but got " + x)
      }
    def extractObject(value: JsValue): (Map[String, JsValue], Vector[String]) =
      value match {
        case x: JsObject =>
          val fields = x.fields
          val vectorBuilder = Vector.newBuilder[String]
          for (field <- fields) {
            vectorBuilder += field._1
          }
          (fields, vectorBuilder.result())
        case JsNull =>
          (Map.empty, Vector.empty)
        case x => deserializationError("Expected Map as JsObject, but got " + x)
      }
  }
}
