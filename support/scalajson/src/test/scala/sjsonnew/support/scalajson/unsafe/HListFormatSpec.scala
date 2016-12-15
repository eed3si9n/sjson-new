package sjsonnew
package support.scalajson
package unsafe

import scala.json.ast.unsafe._
import BasicJsonProtocol._
import org.scalactic._
import org.scalatest._

class HListFormatSpec extends FlatSpec {
  case class Peep(name: String, age: Int)

  import LList.:*:
  type PeepRepr = String :*: Int :*: LNil

  implicit val PeepIso: IsoLList.Aux[Peep, PeepRepr] = LList.iso(
    { p: Peep => ("name", p.name) :*: ("age", p.age) :*: LNil },
    { in: PeepRepr => Peep(in.head, in.tail.head) }
  )

  val bob = Peep("Bob", 23)
  val bobJson = JObject(JField("name", JString("Bob")), JField("age", JNumber(23)))
  val bobJsonStr = """{"name":"Bob","age":23}"""

  // Can't trust unsafe's toString, eg. JObject doesn't nicely toString its fields array, so its toString sucks
  implicit val altPrettifier: Prettifier = Prettifier {
    case j: JValue => j.to_s
    case x         => Prettifier default x
  }

  it should "Peep.toJson correctly"              in assert(bob.toJson === bobJson)
  it should "Peep.toJsonStr correctly"           in assert(bob.toJsonStr === bobJsonStr)
  it should "String.toJson correctly"            in assert(bobJsonStr.toJson === bobJson)
  it should "String.fromJsonStr[Peep] correctly" in assert(bobJsonStr.fromJsonStr[Peep] === bob)
  it should "Peep.jsonRoundTrip correctly"       in assertRoundTrip(bob)

  private def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonRoundTrip)

  implicit class AnyOps[A: JsonWriter](val _x: A) {
    def toJson: JValue    = Converter toJsonUnsafe _x
    def toJsonStr: String = _x.toJson.toJsonStr
  }
  implicit class AnyOps2[A: JsonWriter : JsonReader](val _x: A) {
    def jsonRoundTrip: A = _x.toJson.toJsonStr.toJson.fromJson[A]
  }

  implicit class JValueOps(val _j: JValue) {
    def toJsonStr: String          = CompactPrinter(_j)
    def fromJson[A: JsonReader]: A = Converter fromJsonUnsafe[A] _j

    // scala.json.ast.unsafe doesn't have good toStrings
    def to_s: String = _j match {
      case JNull          => "JNull"
      case JString(value) => s"JString($value)"
      case JNumber(value) => s"JNumber($value)"
      case JTrue          => "JTrue"
      case JFalse         => "JFalse"
      case JObject(value) => value.iterator map (f => s"${f.field}: ${f.value.to_s}") mkString ("JObject(", ", ", ")")
      case JArray(value)  => value.iterator map (_.to_s) mkString ("JArray(", ", ", ")")
    }
  }

  implicit class StringOps(val _s: String) {
    def toJson: JValue                = Parser parseUnsafe _s
    def fromJsonStr[A: JsonReader]: A = _s.toJson.fromJson[A]
  }
}
