package sjsonnew
package shapelesstest

import support.spray._
import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse }
import org.specs2.mutable._
import scala.Right

case class Color(name: String, red: Int, green: Int, blue: Int)


class GenericFormatsSpec extends Specification {
  val color = Color("CadetBlue", 95, 158, 160)

  object CustomProtocol extends BasicJsonProtocol with GenericFormats {
    import GenericFormat._
    implicit val colorFormat = GenericFormat[Color]
  }

  "The case class example" should {
    "behave as expected" in {
      import CustomProtocol._
      assert(colorFormat != null, "colorFormat is null")
      Converter.toJson(color) mustEqual color
    }
  }
}

// .toJson.convertTo[Color]
