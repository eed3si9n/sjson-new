sjson-new
=========

sjson-new is a typeclass based JSON codec, or wit for that [Jawn].

### today's random Philadelphia word

The term *wit* comes from the Philadelphia area.
It's a quick way of ordering a cheesesteak "with" sautéed onions.

### overview

sjson-new consists of two parts:

1. A typeclass-based JSON codec toolkit
2. Support packages which serialize to third-party ASTs

### installation

Here's how to use with Json4s-AST:

```scala
libraryDependencies += "com.eed3si9n" %%  "sjson-new-json4s" % "0.1.0"
```

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
res0: scala.util.Try[XYZ.JsValue] = Success(42)

scala> Converter.fromJson[Int](res0.get)
res1: scala.util.Try[Int] = Success(42)
```

In the above substitute `XYZ` with (`json4s` | `spray`).

A `Converter` object provides the following functions for conversion:

```scala
def toJson[A](obj: A)(implicit writer: JsonWriter[A]): Try[J]
def toJsonUnsafe[A](obj: A)(implicit writer: JsonWriter[A]): J
def fromJson[A](js: J)(implicit reader: JsonReader[A]): Try[A]
def fromJsonUnsafe[A](js: J)(implicit reader: JsonReader[A]): A
```

### dependencies

- `sjson-new-core` has not dependencies other than Scala.
- The support libraries (e.g. `sjon-new-json4s`) depend on the corresponding ASTs they are supporting.

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
* Option, Either, Tuple1 - Tuple22
* List, Array
* immutable.{Map, Iterable, Seq, IndexedSeq, LinearSeq, Set, Vector}
* collection.{Iterable, Seq, IndexedSeq, LinearSeq, Set}
* LList

### LList

sjson-new comes with a datatype called **LList**, which stands for
labelled heterogeneous list.
`List[A]` that comes with the Standard Library can only store values of one type `A`.
Unlike the standard `List[A]`, LList can store values of different types per cell,
and it can also store a label per cell.
Because of this reason, each LList has its own type. Here's how it looks in the REPL:

```scala
scala> import sjsonnew._, LList.:+:
import sjsonnew._
import LList.$colon$plus$colon

scala> import BasicJsonProtocol._
import BasicJsonProtocol._

scala> val x = ("name", "A") :+: ("value", 1) :+: LNil
x: sjsonnew.LList.:+:[String,sjsonnew.LList.:+:[Int,sjsonnew.LNil]] = (name, A) :+: (value, 1) :+: LNil

scala> val y: String :+: Int :+: LNil = x
y: sjsonnew.LList.:+:[String,sjsonnew.LList.:+:[Int,sjsonnew.LNil]] = (name, A) :+: (value, 1) :+: LNil
```

`BasicJsonProtocol` is able to convert all LList values into a JSON object.

### Custom types

In most cases however you'll also want to convert types not covered by the `BasicJsonProtocol`. In these cases you
need to provide `JsonFormat[A]`s for your custom types

All you have to do is provide an isomorphism between your types and an LList using `LList.iso` function.

```scala
scala> import sjsonnew._, LList.:+:
import sjsonnew._
import LList.$colon$plus$colon

scala> import BasicJsonProtocol._
import BasicJsonProtocol._

scala> case class Person(name: String, value: Int)
defined class Person

scala> implicit val personIso = LList.iso(
         { p: Person => ("name", p.name) :+: ("value", p.value) :+: LNil },
         { in: String :+: Int :+: LNil => Person(in.head, in.tail.head) })
personIso: sjsonnew.IsoLList.Aux[Person,sjsonnew.LList.:+:[String,sjsonnew.LList.:+:[Int,sjsonnew.LNil]]] = sjsonnew.IsoLList$$anon$1@4140e9d0

scala> import sjsonnew.support.spray.Converter
import sjsonnew.support.spray.Converter

scala> Converter.toJson[Person](Person("A", 1))
res0: scala.util.Try[spray.json.JsValue] = Success({"name":"A","value":1})
```

Using `personIso`, sjson-new derived the `JsonFomrat` for `Person`.

Suppose now that we have an algebraic datatype (ADT) represented by a sealed trait.
There's a function to compose the `JsonFormat` called `unionFormat2`, `unionFormat3`, ...

```scala
scala> import sjsonnew._, LList.:+:
import sjsonnew._
import LList.$colon$plus$colon

scala> import BasicJsonProtocol._
import BasicJsonProtocol._

scala> :paste
// Entering paste mode (ctrl-D to finish)

sealed trait Contact
case class Person(name: String, value: Int) extends Contact
case class Organization(name: String, value: Int) extends Contact

implicit val personIso = LList.iso(
  { p: Person => ("name", p.name) :+: ("value", p.value) :+: LNil },
  { in: String :+: Int :+: LNil => Person(in.head, in.tail.head) })
implicit val organizationIso = LList.iso(
  { o: Organization => ("name", o.name) :+: ("value", o.value) :+: LNil },
  { in: String :+: Int :+: LNil => Organization(in.head, in.tail.head) })
implicit val ContactFormat = unionFormat2[Contact, Person, Organization]

// Exiting paste mode, now interpreting.

scala> import sjsonnew.support.spray.Converter
import sjsonnew.support.spray.Converter

scala> Converter.toJson[Contact](Organization("Company", 2))
res0: scala.util.Try[spray.json.JsValue] = Success({"value":{"name":"Company","value":2},"type":"Organization"})
```

The `unionFormatN[U, A1, A2, ...]` functions assume that type `U` is the sealed parent trait of the passed in types.
In the JSON object this is encoded by putting the simple type name (just the class name portion) into `type` field.

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
Along with any patches, please state that the patch is your original work and that you license the work to the sjon-new project under the project’s open source license.

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
