/*
 * Copyright (C) 2009-2011 Mathias Doenitz
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
package support.scalajson.unsafe

import java.lang.StringBuilder
import shaded.scalajson.ast.unsafe._

/**
  * A JsonPrinter that produces compact JSON source without any superfluous whitespace.
 */
trait CompactPrinter extends JsonPrinter {

  def print(x: JValue, sb: Appendable): Unit = {
    x match {
      case JObject(x) => printJObject(x, sb)
      case JArray(x)  => printJArray(x, sb)
      case _ => printLeaf(x, sb)
    }
  }

  protected def printJObject(members: Array[JField], sb: Appendable): Unit = {
    sb.append('{')
    printArray(members, sb.append(',')) { m =>
      printString(m.field, sb)
      sb.append(':')
      print(m.value, sb)
    }
    sb.append('}')
  }

  protected def printJArray(elements: Array[JValue], sb: Appendable): Unit = {
    sb.append('[')
    printArray(elements, sb.append(','))(print(_, sb))
    sb.append(']')
  }
}

object CompactPrinter extends CompactPrinter
