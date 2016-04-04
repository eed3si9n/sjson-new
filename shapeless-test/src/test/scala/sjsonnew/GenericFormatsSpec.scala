package sjsonnew

import support.spray._
import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse }
import org.specs2.mutable._
import scala.Right

class GenericFormatsSpec extends Specification {
  case class Color(name: String, red: Int, green: Int, blue: Int)
  val color = Color("CadetBlue", 95, 158, 160)

  "The case class example" should {
    "behave as expected" in {
      object CustomProtocol extends GenericFormats with BasicJsonProtocol {
        implicit val colorFormat: JsonFormat[Color] = GenericFormat[Color]
      }
      import CustomProtocol._
      assert(colorFormat != null)
      Converter.toJson(color) mustEqual color
    }
  }
}

// .toJson.convertTo[Color]
