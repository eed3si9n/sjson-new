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
import java.time._

class CalendarFormatsSpec extends Specification with BasicJsonProtocol {
  "The dateStringIso" should {
    // JDK 8 / Joda dates
    val odt = OffsetDateTime.of(1999, 1, 1, 0, 0, 0, 0, ZoneOffset.of("Z"))
    val omillis = odt.plusNanos(1000 * 1000)
    val zdt = ZonedDateTime.of(1999, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    val zmillis = zdt.plusNanos(1000 * 1000)
    // zoneless instant in time
    val inst = omillis.toInstant
    val ld = LocalDate.of(1999, 1, 1)
    val ldt = LocalDateTime.of(1999, 1, 1, 0, 0, 0, 1000 * 1000)

    // legacy dates
    val seconds = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    seconds.clear
    seconds.set(1999, 1, 1, 0, 0, 0)
    
    val milliseconds = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    milliseconds.clear
    milliseconds.set(1999, 1, 1, 0, 0, 0)
    milliseconds.set(Calendar.MILLISECOND, 1)

    "convert a OffsetDateTime to JsString" in {
      Converter.toJsonUnsafe(odt) mustEqual JsString("1999-01-01T00:00:00Z")
    }

    "convert the JsString back to the OffsetDateTime" in {
      Converter.fromJsonUnsafe[OffsetDateTime](JsString("1999-01-01T00:00:00Z")) mustEqual odt
    }

    "convert a OffsetDateTime with milliseconds to JsString" in {
      Converter.toJsonUnsafe(omillis) mustEqual JsString("1999-01-01T00:00:00.001Z")
    }

    "convert the JsString back to the OffsetDateTime with milliseconds" in {
      Converter.fromJsonUnsafe[OffsetDateTime](JsString("1999-01-01T00:00:00.001Z")) mustEqual omillis
    }

    "convert a ZonedDateTime to JsString" in {
      Converter.toJsonUnsafe(zdt) mustEqual JsString("1999-01-01T00:00:00Z[UTC]")
    }

    "convert the JsString back to the ZonedDateTime" in {
      Converter.fromJsonUnsafe[ZonedDateTime](JsString("1999-01-01T00:00:00Z[UTC]")) mustEqual zdt
    }

    "convert a ZonedDateTime with milliseconds to JsString" in {
      Converter.toJsonUnsafe(zmillis) mustEqual JsString("1999-01-01T00:00:00.001Z[UTC]")
    }

    "convert the JsString back to the ZonedDateTime with milliseconds" in {
      Converter.fromJsonUnsafe[ZonedDateTime](JsString("1999-01-01T00:00:00.001Z[UTC]")) mustEqual zmillis
    }

    "convert an Instant to JsString" in {
      Converter.toJsonUnsafe(inst) mustEqual JsString("1999-01-01T00:00:00.001Z")
    }

    "convert the JsString back to the Instant with milliseconds" in {
      Converter.fromJsonUnsafe[Instant](JsString("1999-01-01T00:00:00.001Z")) mustEqual inst
    }

    "convert a LocalDate to JsString" in {
      Converter.toJsonUnsafe(ld) mustEqual JsString("1999-01-01")
    }
    
    "convert the JsString with no time back to the LocalDate" in {
      Converter.fromJsonUnsafe[LocalDate](JsString("1999-01-01")) mustEqual ld
    }

    "convert a LocalDateTime to JsString" in {
      Converter.toJsonUnsafe(ldt) mustEqual JsString("1999-01-01T00:00:00.001")
    }
    
    "convert the JsString with no time back to the LocalDateTime" in {
      Converter.fromJsonUnsafe[LocalDateTime](JsString("1999-01-01T00:00:00.001")) mustEqual ldt
    }

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
