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
import java.util.{ Calendar, TimeZone }
import java.time._

object CalendarFormatsSpec extends verify.BasicTestSuite with BasicJsonProtocol {
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

  test("convert a OffsetDateTime to JsString") {
    Predef.assert(Converter.toJsonUnsafe(odt) == JsString("1999-01-01T00:00:00Z"))
  }

  test("convert the JsString back to the OffsetDateTime") {
    Predef.assert(Converter.fromJsonUnsafe[OffsetDateTime](JsString("1999-01-01T00:00:00Z")) == odt)
  }

  test("convert a OffsetDateTime with milliseconds to JsString") {
    Predef.assert(Converter.toJsonUnsafe(omillis) == JsString("1999-01-01T00:00:00.001Z"))
  }

  test("convert the JsString back to the OffsetDateTime with milliseconds") {
    Predef.assert(Converter.fromJsonUnsafe[OffsetDateTime](JsString("1999-01-01T00:00:00.001Z")) == omillis)
  }

  test("convert a ZonedDateTime to JsString") {
    Predef.assert(Converter.toJsonUnsafe(zdt) == JsString("1999-01-01T00:00:00Z[UTC]"))
  }

  test("convert the JsString back to the ZonedDateTime") {
    Predef.assert(Converter.fromJsonUnsafe[ZonedDateTime](JsString("1999-01-01T00:00:00Z[UTC]")) == zdt)
  }

  test("convert a ZonedDateTime with milliseconds to JsString") {
    Predef.assert(Converter.toJsonUnsafe(zmillis) == JsString("1999-01-01T00:00:00.001Z[UTC]"))
  }

  test("convert the JsString back to the ZonedDateTime with milliseconds") {
    Predef.assert(Converter.fromJsonUnsafe[ZonedDateTime](JsString("1999-01-01T00:00:00.001Z[UTC]")) == zmillis)
  }

  test("convert an Instant to JsString") {
    Predef.assert(Converter.toJsonUnsafe(inst) == JsString("1999-01-01T00:00:00.001Z"))
  }

  test("convert the JsString back to the Instant with milliseconds") {
    Predef.assert(Converter.fromJsonUnsafe[Instant](JsString("1999-01-01T00:00:00.001Z")) == inst)
  }

  test("convert a LocalDate to JsString") {
    Predef.assert(Converter.toJsonUnsafe(ld) == JsString("1999-01-01"))
  }

  test("convert the JsString with no time back to the LocalDate") {
    Predef.assert(Converter.fromJsonUnsafe[LocalDate](JsString("1999-01-01")) == ld)
  }

  test("convert a LocalDateTime to JsString") {
    Predef.assert(Converter.toJsonUnsafe(ldt) == JsString("1999-01-01T00:00:00.001"))
  }

  test("convert the JsString with no time back to the LocalDateTime") {
    Predef.assert(Converter.fromJsonUnsafe[LocalDateTime](JsString("1999-01-01T00:00:00.001")) == ldt)
  }

  test("convert a Date to JsString") {
    Predef.assert(Converter.toJsonUnsafe(seconds) == JsString("1999-02-01T00:00:00Z"))
  }

  test("convert a Date with milliseconds to JsString") {
    Predef.assert(Converter.toJsonUnsafe(milliseconds) == JsString("1999-02-01T00:00:00.001Z"))
  }

  test("convert the JsString back to the Date") {
    Predef.assert(Converter.fromJsonUnsafe[Calendar](JsString("1999-02-01T00:00:00Z")).getTimeInMillis == seconds.getTimeInMillis)
  }

  test("convert the JsString with milliseconds back to the Date") {
    Predef.assert(Converter.fromJsonUnsafe[Calendar](JsString("1999-02-01T00:00:00.001Z")).getTimeInMillis == milliseconds.getTimeInMillis)
  }

  test("convert the JsString with no time back to the Date") {
    Predef.assert(Converter.fromJsonUnsafe[Calendar](JsString("1999-02-01Z")).getTimeInMillis == seconds.getTimeInMillis)
  }
}
