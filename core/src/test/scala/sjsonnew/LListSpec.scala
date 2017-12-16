package sjsonnew

import org.scalatest.FlatSpec
import BasicJsonProtocol._

final class LListSpec extends FlatSpec {
  private val anLList = "age" -> 23 :*: "name" -> "foo" :*: LNil

  behavior of s"$anLList"

  it should "have _1 === 23" in assert(anLList._1 === 23)
  it should "have _1 !== 42" in assert(anLList._1 !== 42)

  it should "have _2 === foo" in assert(anLList._2 === "foo")
  it should "have _2 !== bar" in assert(anLList._2 !== "bar")

  it should "type error on _3" in assertTypeError("""("age" -> 23 :*: "name" -> "foo" :*: LNil)._3""")
  it should "compile on _2" in assertCompiles("""("age" -> 23 :*: "name" -> "foo" :*: LNil)._2""")
}
