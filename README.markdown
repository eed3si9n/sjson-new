sjson-new
=========

sjson-new is a typeclass based JSON serialization library, or wit for that [Jawn].

### random Philadelphia words time

The term *wit* comes from the Philadelphia area.
It's a quick way of ordering a cheesesteak "with" sautéed onions.

### overview

sjson-new consists of two parts:

1. A typeclass-based JSON serialization toolkit
2. Support packages which serialize to third-party ASTs

### installation

Here's how to use with Spray:

```scala
libraryDependencies += "com.eed3si9n" %%  "sjson-new-spray" % "0.1.0"
```

### converting

sjson-new's converter let's you convert from an object of type `A` to
and an AST of type `J`, given that you provide `JsonFormat[A]`.

This is an example of how you might use the converter into your code:

```scala
scala> import sjsonnew.support.XYZ.Converter
import sjsonnew.support.XYZ.Converter

scala> import sjsonnew.BasicJsonProtocol._
import sjsonnew.BasicJsonProtocol._

scala> Converter.toJson[Int](42)
res0: XYZ.JsValue = 42

scala> Converter.fromJson[Int](res0)
res1: Int = 42
```

In the above substitute `XYZ` with (`"spray"`).

### JsonProtocol

sjson-new uses [sjson]'s Scala-idiomatic typeclass-based approach to connect an existing type `A` with the logic how
to (de)serialize its instances to and from JSON. This notion was further extended by [spray-json], and
sjson-new reuses some of both [sjson] and [spray-json]'s code, see the 'Credits' section below.

While the typeclass in the original [sjson] directly manipulates the JSON AST, sjon-new adds an indirection
mechanism, which allows it to be backend-independent. This machinary is inspired both by [Jawn] and [Scala Pickling].
This wire protocol indirection is called *façade* in Jawn, and "format" in Pickling.
sjson-new will also call this *façade* to avoid the mixup.

The typeclass approach has the advantage of not requiring any change (or even access) to `A`s source code. All (de)serialization
logic is attached *from the outside*. There is no reflection involved, so the resulting conversions are fast. Scalas
excellent type inference reduces verbosity and boilerplate to a minimum, while the Scala compiler will make sure at
compile time that you provided all required (de)serialization logic.

In sjson-new's terminology a *JSON protocol* is nothing but a bunch of implicit values of type `JsonFormat[A]`, whereby
each `JsonFormat[A]` contains the logic of how to convert instance of `A` to and from JSON. All `JsonFormat[A]`s of a
protocol need to be "mece" (mutually exclusive, collectively exhaustive), i.e. they are not allowed to overlap and
together need to span all types required by the application.

This may sound more complicated than it is.
sjon-new comes with a `BasicJsonProtocol`, which already covers all of Scala's value types as well as the most
important reference and collection types. As long as your code uses nothing more than these you only need the
`BasicJsonProtocol`. Here are the types already taken care of by the `BasicJsonProtocol`:

* Byte, Short, Int, Long, Float, Double, Char, Unit, Boolean
* String, Symbol
* BigInt, BigDecimal
* Option, Either, Tuple1 - Tuple7
* List, Array
* immutable.{Map, Iterable, Seq, IndexedSeq, LinearSeq, Set, Vector}
* collection.{Iterable, Seq, IndexedSeq, LinearSeq, Set}

In most cases however you'll also want to convert types not covered by the `BasicJsonProtocol`. In these cases you
need to provide `JsonFormat[A]`s for your custom types. This is not hard at all.

### Providing JsonFormats for Case Classes

If your custom type `A` is a case class then augmenting the `DefaultJsonProtocol` with a `JsonFormat[A]` is really easy:

```scala
case class Color(name: String, red: Int, green: Int, blue: Int)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(Color)
}

import MyJsonProtocol._
import spray.json._

val json = Color("CadetBlue", 95, 158, 160).toJson
val color = json.convertTo[Color]
```

The `jsonFormatX` methods reduce the boilerplate to a minimum, just pass the right one the companion object of your
case class and it will return a ready-to-use `JsonFormat` for your type (the right one is the one matching the number
of arguments to your case class constructor, e.g. if your case class has 13 fields you need to use the `jsonFormat13`
method). The `jsonFormatX` methods try to extract the field names of your case class before calling the more general
`jsonFormat` overloads, which let you specify the field name manually. So, if spray-json has trouble determining the
field names or if your JSON objects use member names that differ from the case class fields you can also use
`jsonFormat` directly.

There is one additional quirk: If you explicitly declare the companion object for your case class the notation above will
stop working. You'll have to explicitly refer to the companion objects `apply` method to fix this:

```scala
case class Color(name: String, red: Int, green: Int, blue: Int)
object Color

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(Color.apply)
}
```

If your case class is generic in that it takes type parameters itself the `jsonFormat` methods can also help you.
However, there is a little more boilerplate required as you need to add context bounds for all type parameters
and explicitly refer to the case classes `apply` method as in this example:

```scala
case class NamedList[A](name: String, items: List[A])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit def namedListFormat[A :JsonFormat] = jsonFormat2(NamedList.apply[A])
}
```

### Providing JsonFormats for other Types

Of course you can also supply (de)serialization logic for types that aren't case classes.
Here is one way to do it:

```scala
class Color(val name: String, val red: Int, val green: Int, val blue: Int)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit object ColorJsonFormat extends RootJsonFormat[Color] {
    def write(c: Color) =
      JsArray(JsString(c.name), JsNumber(c.red), JsNumber(c.green), JsNumber(c.blue))

    def read(value: JsValue) = value match {
      case JsArray(Vector(JsString(name), JsNumber(red), JsNumber(green), JsNumber(blue))) =>
        new Color(name, red.toInt, green.toInt, blue.toInt)
      case _ => deserializationError("Color expected")
    }
  }
}

import MyJsonProtocol._

val json = Color("CadetBlue", 95, 158, 160).toJson
val color = json.convertTo[Color]
```

This serializes `Color` instances as a JSON array, which is compact but does not make the elements semantics explicit.
You need to know that the color components are ordered "red, green, blue".

Another way would be to serialize `Color`s as JSON objects:

```scala
object MyJsonProtocol extends DefaultJsonProtocol {
  implicit object ColorJsonFormat extends RootJsonFormat[Color] {
    def write(c: Color) = JsObject(
      "name" -> JsString(c.name),
      "red" -> JsNumber(c.red),
      "green" -> JsNumber(c.green),
      "blue" -> JsNumber(c.blue)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("name", "red", "green", "blue") match {
        case Seq(JsString(name), JsNumber(red), JsNumber(green), JsNumber(blue)) =>
          new Color(name, red.toInt, green.toInt, blue.toInt)
        case _ => throw new DeserializationException("Color expected")
      }
    }
  }
}
```

This is a bit more verbose in its definition and the resulting JSON but transports the field semantics over to the
JSON side. Note that this is the approach _spray-json_ uses for case classes.


### JsonFormat vs. RootJsonFormat

According to the JSON specification not all of the defined JSON value types are allowed at the root level of a JSON
document. A JSON string for example (like `"foo"`) does not constitute a legal JSON document by itself.
Only JSON objects or JSON arrays are allowed as JSON document roots.

In order to distinguish, on the type-level, "regular" JsonFormats from the ones producing root-level JSON objects or
arrays _spray-json_ defines the [`RootJsonFormat`][1] type, which is nothing but a marker specialization of `JsonFormat`.
Libraries supporting _spray-json_ as a means of document serialization might choose to depend on a `RootJsonFormat[A]`
for a custom type `A` (rather than a "plain" `JsonFormat[A]`), so as to not allow the rendering of illegal document
roots. E.g., the `SprayJsonSupport` trait of _spray-routing_ is one notable example of such a case.

All default converters in the `DefaultJsonProtocol` producing JSON objects or arrays are actually implemented as
`RootJsonFormat`. When "manually" implementing a `JsonFormat` for a custom type `A` (rather than relying on case class
support) you should think about whether you'd like to use instances of `A` as JSON document roots and choose between
a "plain" `JsonFormat` and a `RootJsonFormat` accordingly.

  [1]: http://spray.github.com/spray/api/spray-json/cc/spray/json/RootJsonFormat.html


### JsonFormats for recursive Types

If your type is recursive such as

```scala
case class Foo(i: Int, foo: Foo)
```

you need to wrap your format constructor with `lazyFormat` and supply an explicit type annotation:

```scala
implicit val fooFormat: JsonFormat[Foo] = lazyFormat(jsonFormat(Foo, "i", "foo"))
```

Otherwise your code will either not compile (no explicit type annotation) or throw an NPE at runtime (no `lazyFormat`
wrapper). Note, that `lazyFormat` returns a `JsonFormat` even if it was given a `RootJsonFormat` which means it isn't
picked up by `SprayJsonSupport`. To get back a `RootJsonFormat` just wrap the complete `lazyFormat` call with another
call to `rootFormat`.

### Credits

- In 2010 **Debasish Ghosh** ([@debasishg]) wrote series of blog articles on typeclasses ([Scala Implicits : Type Classes Here I Come
][ghosh1] etc), and implemented typeclass-based JSON serialization in [sjson]. sjson used JSON AST from Dispatch classic, which was contributed by Jorge Ortiz.
- In 2011 **Mathias** ([@sirthias]) created [spray-json] project, combining sjson's serialization code and JSON AST from Dispatch classic, along with PEG-based parser. Honoring sjson, serialization code bares Debasish's copyright.
- Around 2012 to 2014 **Erik Osheim** ([@non]) wrote [Jawn], a backend generic JSON parser. Jawn's facade code is used in sjson-new with further extension.
- In 2013 **Heather Miller**, **Philipp Haller**, and **Eugene Burmako** published [Instant Pickles: Generating Object-Oriented Pickler Combinators for Fast and Extensible Serialization][millar1], and wrote [Scala Pickling], which also provides wire format genericity.

### License

sjson-new is licensed under [APL 2.0].

### Patch Policy

Feedback and contributions to the project, no matter what kind, are always very welcome.
However, patches can only be accepted from their original author.
Along with any patches, please state that the patch is your original work and that you license the work to the
_spray-json_ project under the project’s open source license.

  [@debasishg]: https://github.com/debasishg/
  [@sirthias]: https://github.com/sirthias/
  [@non]: https://github.com/non/
  [ghosh1]: http://debasishg.blogspot.com/2010/06/scala-implicits-type-classes-here-i.html
  [millar1]: http://infoscience.epfl.ch/record/187787/files/oopsla-pickling_1.pdf
  [JSON]: http://json.org
  [repo.spray.io]: http://repo.spray.io
  [sjson]: https://github.com/debasishg/sjson
  [spray-json]: https://github.com/spray/spray-json
  [Databinder-Dispatch]: https://github.com/n8han/Databinder-Dispatch
  [APL 2.0]: http://www.apache.org/licenses/LICENSE-2.0
  [spray-user]: http://groups.google.com/group/spray-user
  [Jawn]: https://github.com/non/jawn
  [Scala Pickling]: https://github.com/scala/pickling

