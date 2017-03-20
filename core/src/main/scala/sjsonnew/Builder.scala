package sjsonnew

import BuilderState._

/**
 * Builder is an mutable structure to write JSON into.
 */
class Builder[J](facade: BuilderFacade[J]) {
  private var resultOpt: Option[J] = None
  private var _state: BuilderState = BuilderState.Begin
  protected var contexts: List[FContext[J]] = Nil
  private var precontext: Option[FContext[J]] = None

  /** Write `Int` value to the current context. */
  def writeInt(x: Int): Unit = writeJ(facade.jint(x))

  /** Write `Long` value to the current context. */
  def writeLong(x: Long): Unit = writeJ(facade.jlong(x))

  /** Write `Double` value to the current context. */
  def writeDouble(x: Double): Unit = writeJ(facade.jdouble(x))

  /** Write `BigDecimal` value to the current context. */
  def writeBigDecimal(x: BigDecimal): Unit = writeJ(facade.jbigdecimal(x))

  /** Write `Boolean` value to the current context. */
  def writeBoolean(x: Boolean): Unit = if (x) writeJ(facade.jtrue()) else writeJ(facade.jfalse())

  /** Write null to the current context. */
  def writeNull(): Unit = writeJ(facade.jnull())

  /** Write `String` value to the current context. */
  def writeString(x: String): Unit =
    state match {
      case InObject =>
        // This is effectively same as addField, but allows to use keyFormat.write.
        if (contexts.isEmpty) serializationError("The builder state is InObject, but the context is empty.")
        else contexts.head.addField(x)
        state = InField
      case _ => writeJ(facade.jstring(x))
    }

  def addField[A](field: String, a: A)(implicit writer: JsonWriter[A]): Unit = writer.addField(field, a, this)

  /** Write field name to the current context. */
  def addFieldName(field: String): Unit =
    state match {
      case InObject =>
        if (contexts.isEmpty) serializationError("The builder state is InObject, but the context is empty.")
        else contexts.head.addField(field)
        state = InField
      case x => stateError(x)
    }

  /** Begins JArray. The builder state will be in `BuilderState.InContext`.
    * Make `writeXXX` calls to write into this array,
    * and end with `endArray`.
    */
  def beginArray(): Unit =
    state match {
      case Begin | InArray | InField =>
        val context = facade.arrayContext()
        contexts ::= context
        state = InArray
      case InObject => stateError(InObject) // expecting field name
      case End => stateError(End)
    }

  /** Ends the current array context.
    */
  def endArray(): Unit =
    state match {
      case InArray =>
        val x = contexts.head
        contexts = contexts.tail
        val js = x.finish
        if (contexts.isEmpty) {
          resultOpt = Some(js)
          state = End
        }
        else {
          if (contexts.head.isObj) state = InField
          else state = InArray
          writeJ(js)
        }
      case x => stateError(x)
    }

  def state: BuilderState = _state
  private def state_=(newState: BuilderState) = _state = newState

  /** Checks if the current state is `InObject` */
  def isInObject: Boolean = state == InObject

  /** Begins JObject. The builder state will be in `BuilderState.InObject`.
    * Make pairs `addField("abc")` and `writeXXX` calls to make field entries,
    * and end with `endObject`.
    */
  def beginObject(): Unit =
    state match {
      case Begin | InArray | InField =>
        val context =
          precontext match {
            case Some(x) =>
              precontext = None
              x
            case _       => facade.objectContext()
          }
        contexts ::= context
        state = InObject
      case InObject => stateError(InObject) // expecting field name
      case End => stateError(End)
    }

  /** Ends the current object context.
    */
  def endObject(): Unit =
    state match {
      case InObject =>
        val x = contexts.head
        contexts = contexts.tail
        val js = x.finish
        if (contexts.isEmpty) {
          resultOpt = Some(js)
          state = End
        }
        else {
          if (contexts.head.isObj) state = InField
          else state = InArray
          writeJ(js)
        }
      case x => stateError(x)
    }

  /** Begins an offline JObject, which will later be used for beginObject().
    * The builder state will be in `BuilderState.InObject`.
    * Make pairs `addField("abc")` and `writeXXX` calls to make field entries,
    * and end with `endObject`.
    */
  def beginPreObject(): Unit =
    state match {
      case Begin | InArray | InField =>
        val p = precontext match {
          case Some(x) => x
          case None    => facade.objectContext()
        }
        contexts ::= p
        state = InObject
      case InObject => stateError(InObject) // expecting field name
      case End => stateError(End)
    }

  /** Ends the current object context.
    */
  def endPreObject(): Unit =
    state match {
      case InObject =>
        val x = contexts.head
        contexts = contexts.tail
        precontext = Some(x)
        if (contexts.isEmpty) {
          state = Begin
        }
        else {
          if (contexts.head.isObj) state = InField
          else state = InArray
        }
      case x => stateError(x)
    }

  private def writeJ(js: J): Unit =
    state match {
      case Begin =>
        resultOpt = Some(js)
        state = End
      case InArray =>
        if (contexts.isEmpty) serializationError("The builder state is InArray, but the context is empty.")
        else contexts.head.add(js)
      case InField =>
        if (contexts.isEmpty) serializationError("The builder state is InField, but the context is empty.")
        else contexts.head.add(js)
        state = InObject
      case InObject => stateError(InObject)
      case End => stateError(End)
    }

  private def stateError(x: BuilderState) = serializationError(s"Unexpected builder state: $x")

  def result: Option[J] = resultOpt
}

sealed trait BuilderState
object BuilderState {
  case object Begin extends BuilderState
  case object End extends BuilderState
  case object InArray extends BuilderState
  case object InObject extends BuilderState
  case object InField extends BuilderState
}
