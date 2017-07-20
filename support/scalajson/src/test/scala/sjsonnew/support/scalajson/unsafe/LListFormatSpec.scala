package sjsonnew
package support.scalajson.unsafe

import shaded.scalajson.ast.unsafe._

import org.scalatest.FlatSpec

import BasicJsonProtocol._

final class LListFormatSpec extends FlatSpec {
  case class Foo(xs: Seq[String])

  implicit val isoLList: IsoLList[Foo] = LList.isoCurried(
    (a: Foo) => "xs" -> a.xs :*: LNil
  ) { case (_, xs) :*: LNil => Foo(xs) }

  val foo        = Foo(Nil)
  val fooLList   = "xs" -> List.empty[String] :*: LNil
  val fooJson    = JObject(JField("$fields", JArray(JString("xs"))), JField("xs", JArray()))
  val fooJsonStr = """{"$fields":["xs"],"xs":[]}"""

  it should "Foo -> LList"        in assert((isoLList to foo) === fooLList)
  it should "Foo -> JSON"         in assert(foo.toJson === fooJson)
  it should "Foo -> JSON string"  in assert(foo.toJsonStr === fooJsonStr)
  it should "JSON string -> JSON" in assert(fooJsonStr.toJson === fooJson)
  it should "JSON string -> Foo"  in assert(fooJsonStr.fromJsonStr[Foo] === foo)
  it should "round trip"          in assertRoundTrip(foo)
  it should "round trip pretty"   in assertPrettyRoundTrip(foo)

  private def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonRoundTrip)
  private def assertPrettyRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonPrettyRoundTrip)
}
