package sjsonnew
package support.scalajson.unsafe

import scala.json.ast.unsafe._

import org.scalatest.FlatSpec

import BasicJsonProtocol._

class ScalaJsonSpec extends FlatSpec {

  "The Parser" should "parse JSON objects" in {
    val json = """{ "x": 5 }"""
    Parser.parseUnsafe(json) match {
      case JObject(fields) =>
        assert(fields.length === 1)
        assert(fields(0) === JField("x", JNumber(5)))

      case _ =>
        fail
    }
  }

  "false" should "round-trip" in assertRoundTrip(false)
  "true" should "round-trip" in assertRoundTrip(true)

  case class Peep(name: String, age: Int)

  import LList.:*:
  type PeepRepr = String :*: Int :*: LNil

  implicit val PeepIso: IsoLList.Aux[Peep, PeepRepr] = LList.iso(
    { p: Peep => ("name", p.name) :*: ("age", p.age) :*: LNil },
    { in: PeepRepr => Peep(in.head, in.tail.head) }
  )

  val bob = Peep("Bob", 23)
  val bobJson = JObject(JField("$fields", JArray(JString("name"), JString("age"))),
    JField("name", JString("Bob")), JField("age", JNumber(23)))
  val bobJsonStr = """{"$fields":["name","age"],"name":"Bob","age":23}"""

  it should "Peep.toJson correctly"              in assert(bob.toJson === bobJson)
  it should "Peep.toJsonStr correctly"           in assert(bob.toJsonStr === bobJsonStr)
  it should "String.toJson correctly"            in assert(bobJsonStr.toJson === bobJson)
  it should "String.fromJsonStr[Peep] correctly" in assert(bobJsonStr.fromJsonStr[Peep] === bob)
  it should "Peep.jsonRoundTrip correctly"       in assertRoundTrip(bob)

  private def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonRoundTrip)
}
