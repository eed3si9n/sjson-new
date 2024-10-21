package sjsonnew
package support.scalajson

import java.io.{ByteArrayOutputStream, OutputStreamWriter}

import shaded.scalajson.ast.unsafe._
import org.scalactic._

package object unsafe {
  implicit class AnyOps[A: JsonWriter](_x: A) {
    def toJson: JValue    = Converter toJsonUnsafe _x
    def toJsonStr: String = _x.toJson.toJsonStr
  }
  implicit class AnyOps2[A: JsonWriter : JsonReader](_x: A) {
    def jsonRoundTrip: A = _x.toJson.toJsonStr.toJson.fromJson[A]
    def jsonPrettyRoundTrip: A = _x.toJson.toPrettyStr.toJson.fromJson[A]
    def jsonBinaryRoundTrip: A = _x.toJson.toBinary.toJson.fromJson[A]
  }

  implicit class JValueOps(private val _j: JValue) extends AnyVal {
    def toJsonStr: String          = CompactPrinter(_j)
    def toPrettyStr: String        = PrettyPrinter(_j)
    def toBinary: Array[Byte]      = {
      val baos = new ByteArrayOutputStream()
      val xpto = new OutputStreamWriter(baos)
      CompactPrinter.print(_j, xpto)
      xpto.close()
      baos.toByteArray
    }
    def fromJson[A: JsonReader]: A = Converter.fromJsonUnsafe[A](_j)

    // scalajson.ast.unsafe doesn't have good toStrings
    def to_s: String = _j match {
      case JNull          => "JNull"
      case JString(value) => s"JString($value)"
      case JNumber(value) => s"JNumber($value)"
      case JTrue          => "JTrue"
      case JFalse         => "JFalse"
      case JObject(value) => value.iterator.map(f => s"${f.field}: ${f.value.to_s}").mkString("JObject(", ", ", ")")
      case JArray(value)  => value.iterator.map(_.to_s).mkString("JArray(", ", ", ")")
    }
  }

  implicit class StringOps(private val _s: String) extends AnyVal {
    def toJson: JValue                = Parser parseUnsafe _s
    def fromJsonStr[A: JsonReader]: A = _s.toJson.fromJson[A]
  }

  implicit class ByteArrayOps(private val a: Array[Byte]) extends AnyVal {
    def toJson: JValue                = Parser.parseFromByteBuffer(java.nio.ByteBuffer.wrap(a)).get
  }

  // Can't trust unsafe's toString, eg. JObject doesn't nicely toString its fields array, so its toString sucks
  implicit val altPrettifier: Prettifier = Prettifier {
    case j: JValue => j.to_s
    case x         => Prettifier default x
  }
}
