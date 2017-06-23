package sjsonnew

import scala.annotation.implicitNotFound

/** A typeclass for converting values of type `A` to strings, used for encoding instances of `Map[A, ?]`. */
@implicitNotFound(msg = "Cannot find JsonKeyWriter or JsonKeyFormat type class for ${A}")
trait JsonKeyWriter[A] { self =>
  def write(key: A): String
  def contramap[B](f: B => A): JsonKeyWriter[B] =
    new JsonKeyWriter[B] {
      def write(key: B): String =
        self.write(f(key))
    }
}

/** A typeclass for converting strings to values of type `A`, used for decoding instances of `Map[A, ?]`. */
@implicitNotFound(msg = "Cannot find JsonKeyReader or JsonKeyFormat type class for ${A}")
trait JsonKeyReader[A] { self =>
  def read(key: String): A
  def map[B](f: A => B): JsonKeyReader[B] =
    new JsonKeyReader[B] {
      def read(key: String): B =
        f(self.read(key))
    }
}

/** A typeclasses for encoding and decoding instances of `Map[A, ?]`. */
trait JsonKeyFormat[A] extends JsonKeyWriter[A] with JsonKeyReader[A]

object JsonKeyFormat {
  def apply[A](w: A => String, r: String => A): JsonKeyFormat[A] = new JsonKeyFormat[A] {
    def write(key: A) = w(key)
    def read(key: String) = r(key)
  }
}
