/*
 * Original implementation (C) 2009-2011 Debasish Ghosh
 * Adapted and extended in 2011 by Mathias Doenitz
 * Adapted and extended in 2016 by Eugene Yokota
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sjsonnew

trait CollectionFormats {
  /**
    * Supplies the JsonFormat for Lists.
   */
  implicit def listFormat[A: JsonFormat] = new RootJsonFormat[List[A]] {
    lazy val elemFormat = implicitly[JsonFormat[A]]
    def write[J](list: List[A], builder: Builder[J], facade: Facade[J]): Unit = {
      list foreach { x => elemFormat.write(x, builder, facade) }
      val context = facade.arrayContext()
      val xs = builder.convertContexts
      builder.clear()
      xs foreach { x => context.add(x) }
      builder.add(context)
    }
    def read[J](value: J, facade: Facade[J]): List[A] = {
      val elems = facade.extractArray(value)
      (elems map { elem =>
        elemFormat.read(elem, facade)
      }).toList
    }
  }

  /**
    * Supplies the JsonFormat for Arrays.
   */
  implicit def arrayFormat[A: JsonFormat: ClassManifest] = new RootJsonFormat[Array[A]] {
    lazy val elemFormat = implicitly[JsonFormat[A]]
    def write[J](array: Array[A], builder: Builder[J], facade: Facade[J]): Unit = {
      array foreach { x => elemFormat.write(x, builder, facade) }
      val context = facade.arrayContext()
      val xs = builder.convertContexts
      builder.clear()
      xs foreach { x => context.add(x) }
      builder.add(context)
    }
    def read[J](value: J, facade: Facade[J]): Array[A] = {
      val elems = facade.extractArray(value)
      (elems map { elem =>
        elemFormat.read(elem, facade)
      }).toArray[A]
    }
  }

  /**
    * Supplies the JsonFormat for Maps. The implicitly available JsonFormat for the key type K must
    * always write JsStrings, otherwise a [[sjsonnew.SerializationException]] will be thrown.
   */
  implicit def mapFormat[K: JsonFormat, V: JsonFormat] = new RootJsonFormat[Map[K, V]] {
    lazy val keyFormat = implicitly[JsonFormat[K]]
    lazy val valueFormat = implicitly[JsonFormat[V]]
    def write[J](m: Map[K, V], builder: Builder[J], facade: Facade[J]): Unit = {
      m foreach {
        case (k, v) =>
          keyFormat.write(k, builder, facade)
          valueFormat.write(v, builder, facade)
      }
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
    def read[J](value: J, facade: Facade[J]): Map[K, V] = {
      val fields = facade.extractObject(value)
      Map(fields map {
        case (kStr, v) =>
          val k = facade.jstring(kStr)
          keyFormat.read(k, facade) -> valueFormat.read(v, facade)
      }: _*)
    }
  }

  import collection.{immutable => imm}

  implicit def immIterableFormat[T :JsonFormat]   = viaSeq[imm.Iterable[T], T](seq => imm.Iterable(seq :_*))
  implicit def immSeqFormat[T :JsonFormat]        = viaSeq[imm.Seq[T], T](seq => imm.Seq(seq :_*))
  implicit def immIndexedSeqFormat[T :JsonFormat] = viaSeq[imm.IndexedSeq[T], T](seq => imm.IndexedSeq(seq :_*))
  implicit def immLinearSeqFormat[T :JsonFormat]  = viaSeq[imm.LinearSeq[T], T](seq => imm.LinearSeq(seq :_*))
  implicit def immSetFormat[T :JsonFormat]        = viaSeq[imm.Set[T], T](seq => imm.Set(seq :_*))
  implicit def vectorFormat[T :JsonFormat]        = viaSeq[Vector[T], T](seq => Vector(seq :_*))

  import collection._

  implicit def iterableFormat[T :JsonFormat]   = viaSeq[Iterable[T], T](seq => Iterable(seq :_*))
  implicit def seqFormat[T :JsonFormat]        = viaSeq[Seq[T], T](seq => Seq(seq :_*))
  implicit def indexedSeqFormat[T :JsonFormat] = viaSeq[IndexedSeq[T], T](seq => IndexedSeq(seq :_*))
  implicit def linearSeqFormat[T :JsonFormat]  = viaSeq[LinearSeq[T], T](seq => LinearSeq(seq :_*))
  implicit def setFormat[T :JsonFormat]        = viaSeq[Set[T], T](seq => Set(seq :_*))

  /**
    * A JsonFormat construction helper that creates a JsonFormat for an Iterable type I from a builder function
    * Seq => I.
   */
  def viaSeq[I <: Iterable[A], A: JsonFormat](f: imm.Seq[A] => I): RootJsonFormat[I] = new RootJsonFormat[I] {
    lazy val elemFormat = implicitly[JsonFormat[A]]
    def write[J](iterable: I, builder: Builder[J], facade: Facade[J]): Unit = {
      iterable foreach { x => elemFormat.write(x, builder, facade) }
      val context = facade.arrayContext()
      val xs = builder.convertContexts
      builder.clear()
      xs foreach { x => context.add(x) }
      builder.add(context)
    }
    def read[J](value: J, facade: Facade[J]): I = {
      val elems = facade.extractArray(value)
      f(elems map { elem =>
        elemFormat.read(elem, facade)
      })
    }
  }
}
