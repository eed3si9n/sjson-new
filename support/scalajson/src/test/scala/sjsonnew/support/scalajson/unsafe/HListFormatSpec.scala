package sjsonnew
package support.scalajson
package unsafe

import shaded.scalajson.ast.unsafe._
import org.scalatest.flatspec.AnyFlatSpec

class HListFormatSpec extends AnyFlatSpec {
  case class Peep(name: String, age: Int)

  import HList.:+:
  implicit val PeepFormat: JsonFormat[Peep] = {
    projectFormat[Peep, String :+: Int :+: HNil](
      p => p.name :+: p.age :+: HNil,
      { case name :+: age :+: HNil => Peep(name, age) }
    )
  }

  val bob = Peep("Bob", 23)
  val bobJson = JArray(JString("Bob"), JNumber(23))
  val bobJsonStr = """["Bob",23]"""

  it should "HNil.jsonRoundTrip correctly"                      in assertRoundTrip(HNil)
  it should "(23 :+: HNil).jsonRoundTrip correctly"             in assertRoundTrip(23 :+: HNil)
  it should "(23 :+: \"foo\" :+: HNil).jsonRoundTrip correctly" in assertRoundTrip(23 :+: "foo" :+: HNil)

  it should "Peep.toJson correctly"              in assert(bob.toJson === bobJson)
  it should "Peep.toJsonStr correctly"           in assert(bob.toJsonStr === bobJsonStr)
  it should "String.toJson correctly"            in assert(bobJsonStr.toJson === bobJson)
  it should "String.fromJsonStr[Peep] correctly" in assert(bobJsonStr.fromJsonStr[Peep] === bob)
  it should "Peep.jsonRoundTrip correctly"       in assertRoundTrip(bob)

  it should "HList.jsonRoundTrip correctly" in assertRoundTrip(bob :+: Peep("Amy", 22) :+: HNil)

  private def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonRoundTrip)
}
