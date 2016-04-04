package sjsonnew

import shapeless.{ `::` => :#:, _ }
import poly._

trait GenericEmpty {
  def emptyProduct: JsonFormat[HNil] = new RootJsonFormat[HNil] {
    def write[J](m: HNil, builder: Builder[J], facade: Facade[J]): Unit = {
      val xs = builder.convertContexts
      builder.clear()
      val context = facade.objectContext()
      if (xs.size % 2 == 1) serializationError(s"Expected even number of fields but contains ${xs.size}")
      xs.grouped(2) foreach {
        case List(k, v) =>
          val keyStr = (try {
            facade.extractString(k)
          } catch {
            case DeserializationException(msg, _, _) => serializationError(s"Map key must be formatted as JString, not '$k'")
          })
          context.add(keyStr)
          context.add(v)
      }
      builder.add(context)
    }
    def read[J](value: J, facade: Facade[J]): HNil = HNil
  }
}

trait GenericFormats {
  object GenericFormat extends LabelledProductTypeClassCompanion[JsonFormat] {
    object typeClass extends LabelledProductTypeClass[JsonFormat] with GenericEmpty {
      def product[F, T <: HList](name: String, FHead: JsonFormat[F], FTail: JsonFormat[T]) = new RootJsonFormat[F :#: T] {
        def write[J](m: F :#: T, builder: Builder[J], facade: Facade[J]): Unit = {
          m match {
            case head :#: tail =>
              val context = facade.singleContext()
              context.add(facade.jstring(name))
              builder.add(context)
              FHead.write(head, builder, facade)
              FTail.write(tail, builder, facade)
          }
        }
        def read[J](value: J, facade: Facade[J]): F :#: T = {
          val fields = facade.extractObject(value)
          val elem = fields find { case (k, v) => k == name } match {
            case Some((k, v)) => v
            case _ => deserializationError(s"Field $name is not found: $value")
          }
          val head = FHead.read(elem, facade)
          val tail = FTail.read(value, facade)
          head :: tail
        }
      }

      def project[A1, A2](instance: => JsonFormat[A2], to: A1 => A2, from: A2 => A1) = new JsonFormat[A1] {
        def write[J](a1: A1, builder: Builder[J], facade: Facade[J]): Unit =
          instance.write(to(a1), builder, facade)
        def read[J](value: J, facade: Facade[J]): A1 =
          from(instance.read(value, facade))
      }
    }
  }
}

// object Generic extends GenericInstances
