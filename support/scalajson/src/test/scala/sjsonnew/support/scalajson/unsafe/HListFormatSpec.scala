package sjsonnew
package support.scalajson
package unsafe

import scala.json.ast.unsafe._
import BasicJsonProtocol._
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

  it should "Peep.toJson correctly"              in assert(bob.toJson === bobJson)
  it should "Peep.toJsonStr correctly"           in assert(bob.toJsonStr === bobJsonStr)
  it should "String.toJson correctly"            in assert(bobJsonStr.toJson === bobJson)
  it should "String.fromJsonStr[Peep] correctly" in assert(bobJsonStr.fromJsonStr[Peep] === bob)
  it should "Peep.jsonRoundTrip correctly"       in assertRoundTrip(bob)

  it should "HList.jsonRoundTrip correctly" in assertRoundTrip(bob :+: Peep("Amy", 22) :+: HNil)

  private def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonRoundTrip)
}
