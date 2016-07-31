/*
 * Copyright (C) 2016 Eugene Yokota
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
package support.spray

import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse, JsObject }
import org.specs2.mutable._
import java.util.{ Calendar, TimeZone }

class CalendarFormatsSpec extends Specification with BasicJsonProtocol {
  "The dateStringIso" should {
    val seconds = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    seconds.clear
    seconds.set(1999, 1, 1, 0, 0, 0)
    val milliseconds = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    milliseconds.clear
    milliseconds.set(1999, 1, 1, 0, 0, 0)
    milliseconds.set(Calendar.MILLISECOND, 1)
    "convert a Date to JsString" in {
      Converter.toJsonUnsafe(seconds) mustEqual JsString("1999-02-01T00:00:00Z")
    }
    "convert a Date with milliseconds to JsString" in {
      Converter.toJsonUnsafe(milliseconds) mustEqual JsString("1999-02-01T00:00:00.001Z")
    }
    "convert the JsString back to the Date" in {
      Converter.fromJsonUnsafe[Calendar](JsString("1999-02-01T00:00:00Z")).getTimeInMillis mustEqual seconds.getTimeInMillis
    }
    "convert the JsString with milliseconds back to the Date" in {
      Converter.fromJsonUnsafe[Calendar](JsString("1999-02-01T00:00:00.001Z")).getTimeInMillis mustEqual milliseconds.getTimeInMillis
    }
    "convert the JsString with no time back to the Date" in {
      Converter.fromJsonUnsafe[Calendar](JsString("1999-02-01Z")).getTimeInMillis mustEqual seconds.getTimeInMillis
    }
  }
}
