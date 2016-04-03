package sjsonnew

import scala.collection.mutable

/**
 * Builder is an mutable structure to write JSON into.
 */
class Builder[J] {
  protected val contexts: mutable.ListBuffer[FContext[J]] = mutable.ListBuffer.empty
  def add(context: FContext[J]): Unit = contexts.append(context)
  def result: Option[J] =
    contexts.headOption map { _.finish }
  def convertContexts: List[J] =
    contexts.toList map { _.finish }
  def clear(): Unit = contexts.clear()
}
