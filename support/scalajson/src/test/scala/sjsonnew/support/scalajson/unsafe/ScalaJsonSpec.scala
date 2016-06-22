package sjsonnew
package support.scalajson.unsafe

import scala.json.ast.unsafe._

import org.scalatest.FlatSpec

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
}
