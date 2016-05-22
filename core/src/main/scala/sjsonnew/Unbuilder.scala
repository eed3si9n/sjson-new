package sjsonnew

import scala.collection.mutable

import UnbuilderState._

/**
 * Builder is an mutable structure to write JSON into.
 */
class Unbuilder[J](facade: Facade[J]) {
  private var state: UnbuilderState = UnbuilderState.Begin
  private val contexts: mutable.Stack[UnbuilderContext[J]] = mutable.Stack.empty[UnbuilderContext[J]]

  /** Read `Int` value to the current context. */
  def readInt(js: J): Int =
    readJ[Int](js, facade.extractInt)
  /** Read `Long` value to the current context. */
  def readLong(js: J): Long =
    readJ[Long](js, facade.extractLong)
  /** Read `Double` value to the current context. */
  def readDouble(js: J): Double =
    readJ[Double](js, facade.extractDouble)
  /** Read `Float` value to the current context. */
  def readFloat(js: J): Float =
    readJ[Float](js, facade.extractFloat)
  /** Read `BigDecimal` value to the current context. */
  def readBigDecimal(js: J): BigDecimal =
    readJ[BigDecimal](js, facade.extractBigDecimal)
  /** Read `Boolean` value to the current context. */
  def readBoolean(js: J): Boolean =
    readJ[Boolean](js, facade.extractBoolean)
  /** Read `String` value to the current context. */
  def readString(js: J): String =
    readJ[String](js, facade.extractString)
  /** Check if js is null */
  def isJnull(js: J): Boolean =
    facade.isJnull(js)
  /** Begin reading JSON array. Returns the size.
    * Call `nextElement` n-times, and then call `endArray`.
    */
  def beginArray(js: J): Int =
    state match {
      case Begin | InArray | InObject =>
        val context = UnbuilderContext.ArrayContext(facade.extractArray(js))
        contexts.push(context)
        state = InArray
        context.elements.size
      case End => stateError(End)
    }
  def nextElement: J =
    state match {
      case InArray =>
        contexts.top match {
          case ctx: UnbuilderContext.ArrayContext[J] => ctx.next
          case x => deserializationError(s"Unexpected context: $x")
        }
      case x => stateError(x)
    }
  /** End reading JSON array. Returns the size. */
  def endArray(): Unit =
    state match {
      case InArray =>
        contexts.pop
        if (contexts.isEmpty) state = End
        else contexts.top match {
          case _: UnbuilderContext.ObjectContext[J] => state = InObject
          case _ => state = InArray
        }
      case x => stateError(x)
    }
  /** Begin reading JSON object. Returns the size.
    * Call `nextField` n-times, and then call `endObject`.
    */
  def beginObject(js: J): Int =
    state match {
      case Begin | InArray | InObject =>
        val context = UnbuilderContext.ObjectContext(facade.extractObject(js))
        contexts.push(context)
        state = InObject
        context.fields.size
      case End => stateError(End)
    }
  def nextFeild(): (String, J) =
    state match {
      case InObject =>
        contexts.top match {
          case ctx: UnbuilderContext.ObjectContext[J] => ctx.next
          case x => deserializationError(s"Unexpected context: $x")
        }
      case x => stateError(x)
    }
  def nextFeildWithJString(): (J, J) =
    nextFeild match {
      case (k, v) => (facade.jstring(k), v)
    }
  /** End reading JSON object. Returns the size. */
  def endObject(): Unit =
    state match {
      case InObject =>
        contexts.pop
        if (contexts.isEmpty) state = End
        else contexts.top match {
          case _: UnbuilderContext.ObjectContext[J] => state = InObject
          case _ => state = InArray
        }
      case x => stateError(x)
    }
  private def readJ[A](js: J, f: J => A): A =
    state match {
      case Begin =>
        val x = f(js)
        state = End
        x
      case InArray =>
        if (contexts.isEmpty) deserializationError("The unbuilder state is InArray, but the context is empty.")
        else f(js)
      case InObject =>
        if (contexts.isEmpty) deserializationError("The unbuilder state is InField, but the context is empty.")
        else f(js)
      case End => stateError(End)
    }
  private def stateError(x: UnbuilderState) = deserializationError(s"Unexpected builder state: $x")
}

private[sjsonnew] sealed trait UnbuilderState
private[sjsonnew] object UnbuilderState {
  case object Begin extends UnbuilderState
  case object End extends UnbuilderState
  case object InArray extends UnbuilderState
  case object InObject extends UnbuilderState
}

private[sjsonnew] trait UnbuilderContext[J]
private[sjsonnew] object UnbuilderContext {
  case class ObjectContext[J](fields: Vector[(String, J)]) extends UnbuilderContext[J] {
    private var idx: Int = 0
    def next: (String, J) = {
      val x = fields(idx)
      idx = idx + 1
      x
    }
  }
  case class ArrayContext[J](elements: Vector[J]) extends UnbuilderContext[J] {
    private var idx: Int = 0
    def next: J = {
      val x = elements(idx)
      idx = idx + 1
      x
    }
  }
}
