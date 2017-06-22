/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 * Adapted and extended in 2017 by Eugene Yokota
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
package support.scalajson.unsafe

import java.lang.StringBuilder
import annotation.tailrec
import scala.json.ast.unsafe._

/**
  * A JsonPrinter that produces a nicely readable JSON source.
 */
trait PrettyPrinter extends JsonPrinter {
  val Indent = 2

  def print(x: JValue, sb: StringBuilder) {
    print(x, sb, 0)
  }
  
  protected def print(x: JValue, sb: StringBuilder, indent: Int) {
    x match {
      case JObject(x) => printJObject(x, sb, indent)
      case JArray(x)  => printJArray(x, sb, indent)
      case _          => printLeaf(x, sb)
    }
  }

  protected def printJObject(members: Array[JField], sb: StringBuilder, indent: Int) {
    sb.append("{\n")    
    printArray(members, sb.append(",\n")) { m =>
      printIndent(sb, indent + Indent)
      printString(m.field, sb)
      sb.append(": ")
      print(m.value, sb, indent + Indent)
    }
    sb.append('\n')
    printIndent(sb, indent)
    sb.append("}")
  }
  
  protected def printJArray(elements: Array[JValue], sb: StringBuilder, indent: Int) {
    sb.append('[')
    printArray(elements, sb.append(", "))(print(_, sb, indent))
    sb.append(']')
  }
  
  protected def printIndent(sb: StringBuilder, indent: Int) {
    @tailrec def rec(indent: Int): Unit =
      if (indent > 0) {
        sb.append(' ')
        rec(indent - 1)
      }
    rec(indent)
  }
}

object PrettyPrinter extends PrettyPrinter
