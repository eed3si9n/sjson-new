package sjsonnew

import scala.collection.mutable

import BuilderState._

/**
 * Builder is an mutable structure to write JSON into.
 */
class Builder[J] {
  private var resultOpt: Option[J] = None
  private var state: BuilderState = BuilderState.Begin
  protected val contexts: mutable.Stack[FContext[J]] = mutable.Stack.empty[FContext[J]]

  /** Write `Int` value to the current context. */
  def writeInt(x: Int)(implicit facade: Facade[J]): Unit =
    writeJ(facade.jint(x))
  /** Write `Long` value to the current context. */
  def writeLong(x: Long)(implicit facade: Facade[J]): Unit =
    writeJ(facade.jlong(x))
  /** Write `Double` value to the current context. */
  def writeDouble(x: Double)(implicit facade: Facade[J]): Unit =
    writeJ(facade.jdouble(x))
  /** Write `BigDecimal` value to the current context. */
  def writeBigDecimal(x: BigDecimal)(implicit facade: Facade[J]): Unit =
    writeJ(facade.jbigdecimal(x))
  /** Write `Boolean` value to the current context. */
  def writeBoolean(x: Boolean)(implicit facade: Facade[J]): Unit =
    if (x) writeJ(facade.jtrue())
    else writeJ(facade.jfalse())
  /** Write null to the current context. */
  def writeNull()(implicit facade: Facade[J]): Unit =
    writeJ(facade.jnull())
  /** Write `String` value to the current context. */
  def writeString(x: String)(implicit facade: Facade[J]): Unit =
    state match {
      case InObject =>
        if (contexts.isEmpty) serializationError("The builder state is InObject, but the context is empty.")
        else contexts.top.add(x)
        state = InField
      case _ => writeJ(facade.jstring(x))
    }

  /** Begins JArray. The builder state will be in BuilderState.InContext,
    * and further `writeXXX` calls will be written into this array.
    */
  def beginArray()(implicit facade: Facade[J]): Unit =
    state match {
      case Begin | InArray | InField =>
        val context = facade.arrayContext()
        contexts.push(context)
        state = InArray
      case InObject => stateError(InObject) // expecting field name
      case End => stateError(End)
    }
  /** Ends the current array context.
    */
  def endArray()(implicit facade: Facade[J]): Unit =
    state match {
      case InArray =>
        val x = contexts.pop
        val js = x.finish
        if (contexts.isEmpty) {
          resultOpt = Some(js)
          state = End
        }
        else {
          if (contexts.top.isObj) state = InField
          else state = InArray
          writeJ(js)
        }
      case x => stateError(x)
    }

  def beginObject()(implicit facade: Facade[J]): Unit =
    state match {
      case Begin | InArray | InField =>
        val context = facade.objectContext()
        contexts.push(context)
        state = InObject
      case InObject => stateError(InObject) // expecting field name
      case End => stateError(End)
    }
  /** Ends the current array context.
    */
  def endObject()(implicit facade: Facade[J]): Unit =
    state match {
      case InObject =>
        val x = contexts.pop
        val js = x.finish
        if (contexts.isEmpty) {
          resultOpt = Some(js)
          state = End
        }
        else {
          if (contexts.top.isObj) state = InField
          else state = InArray
          writeJ(js)
        }
      case x => stateError(x)
    }

  private def writeJ(js: J)(implicit facade: Facade[J]): Unit =
    state match {
      case Begin =>
        resultOpt = Some(js)
        state = End
      case InArray =>
        if (contexts.isEmpty) serializationError("The builder state is InArray, but the context is empty.")
        else contexts.top.add(js)
      case InField =>
        if (contexts.isEmpty) serializationError("The builder state is InField, but the context is empty.")
        else contexts.top.add(js)
        state = InObject
      case InObject => stateError(InObject)
      case End => stateError(End)
    }
  private def stateError(x: BuilderState) = serializationError(s"Unexpected builder state: $x")
  def result: Option[J] = resultOpt
}

private[sjsonnew] sealed trait BuilderState
private[sjsonnew] object BuilderState {
  case object Begin extends BuilderState
  case object End extends BuilderState
  case object InArray extends BuilderState
  case object InObject extends BuilderState
  case object InField extends BuilderState
}
