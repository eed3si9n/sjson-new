package sjsonnew
package support.scalajson.unsafe

import shaded.scalajson.ast.unsafe._

import org.scalatest.flatspec.AnyFlatSpec

import BasicJsonProtocol._

final class LListFormatSpec extends AnyFlatSpec {
  case class Foo(xs: Seq[String])

  implicit val fooIso: IsoLList[Foo] = LList.isoCurried(
    (a: Foo) => "xs" -> a.xs :*: LNil
  ) { case (_, xs) :*: LNil => Foo(xs) }

  case class Bar(kv: Map[String, String], x: String)

  implicit val barIso: IsoLList[Bar] = LList.isoCurried(
    (a: Bar) => "kv" -> a.kv :*: "x" -> a.x :*: LNil
  ) { case (_, kv) :*: (_, x) :*: LNil => Bar(kv, x) }

  val foo        = Foo(Nil)
  val fooLList   = "xs" -> List.empty[String] :*: LNil
  val fooJson    = JObject(JField("$fields", JArray(JString("xs"))), JField("xs", JArray()))
  val fooJsonStr = """{"$fields":["xs"],"xs":[]}"""
  val bar        = Bar(Map.empty, "bar")

  it should "Foo -> LList"        in assert((fooIso to foo) === fooLList)
  it should "Foo -> JSON"         in assert(foo.toJson === fooJson)
  it should "Foo -> JSON string"  in assert(foo.toJsonStr === fooJsonStr)
  it should "JSON string -> JSON" in assert(fooJsonStr.toJson === fooJson)
  it should "JSON string -> Foo"  in assert(fooJsonStr.fromJsonStr[Foo] === foo)
  it should "round trip"          in assertRoundTrip(foo)
  it should "round trip pretty"   in assertPrettyRoundTrip(foo)
  it should "round trip map"      in assertRoundTrip(bar)

  private def assertRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonRoundTrip)
  private def assertPrettyRoundTrip[A: JsonWriter : JsonReader](x: A) = assert(x === x.jsonPrettyRoundTrip)
}
