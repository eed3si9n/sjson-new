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

  def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = {
    val jValue1: JValue = Converter.toJson(x).get
    val jsonString: String = CompactPrinter(jValue1)
    val jValue2: JValue = Parser.parseUnsafe(jsonString)
    val y: A = Converter.fromJson[A](jValue2).get
    assert(x === y)
  }
}
