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

import java.util.{ Calendar, GregorianCalendar }
import java.time._
import java.time.format.DateTimeFormatter

trait CalendarFormats {
  self: IsoFormats =>

  private val utc: ZoneId = ZoneId.of("UTC")

  /** This output is ISO 8601 compilant, e.g. 1999-01-01T00:00:00.001Z */
  implicit val offsetDateTimeStringIso: IsoString[OffsetDateTime] = {
    IsoString.iso[OffsetDateTime]( (odt: OffsetDateTime) => {
        val datetimefmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        odt.format(datetimefmt)
      },
      (s: String) => {
        val datetimefmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        OffsetDateTime.parse(s, datetimefmt)
      })
  }

  /** This output is ISO 8601 compilant, e.g. 1999-01-01T00:00:00.001Z */
  implicit val instantStringIso: IsoString[Instant] = {
    IsoString.iso[Instant]( (i: Instant) => {
        val datetimefmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val odt = OffsetDateTime.ofInstant(i, utc)
        odt.format(datetimefmt)
      },
      (s: String) => {
        val datetimefmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val odt = OffsetDateTime.parse(s, datetimefmt)
        odt.toInstant
      })
  }

  /** This output is ISO 8601 compilant, e.g. 1999-01-01T00:00:00.001Z */
  implicit val calendarStringIso: IsoString[Calendar] = {
    IsoString.iso[Calendar]( (c: Calendar) => {
        val datetimefmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        // https://docs.oracle.com/javase/tutorial/datetime/iso/legacy.html
        val i = c.toInstant
        val tz = c.getTimeZone.toZoneId
        val zdt = ZonedDateTime.ofInstant(i, tz)
        zdt.format(datetimefmt)
      },
      (s: String) => {
        val datetimefmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val datefmt = DateTimeFormatter.ISO_DATE
        val zdt = if (s contains "T") ZonedDateTime.parse(s, datetimefmt)         
                  else {
                    val ld = LocalDate.parse(s, datefmt)
                    ld.atStartOfDay(utc)
                  }
        GregorianCalendar.from(zdt)
      })
  }

  /**
   * As far as I know, the output this produces is not ISO 8601 compilant.
   * It appends timezone designator e.g. 1999-01-01T00:00:00.001Z[UTC],
   * which is used to identify the ZoneId, which denotes the local summer time rules etc.
   */
  implicit val zonedDateTimeStringIso: IsoString[ZonedDateTime] =
    IsoString.iso[ZonedDateTime]( (zdt: ZonedDateTime) => {
      val datetimefmt = DateTimeFormatter.ISO_DATE_TIME
      zdt.format(datetimefmt)
    },
    (s: String) => {
      val datetimefmt = DateTimeFormatter.ISO_DATE_TIME
      val datefmt = DateTimeFormatter.ISO_DATE
      if (s contains "T") {
        ZonedDateTime.parse(s, datetimefmt)         
      } else {
        val ld = LocalDate.parse(s, datefmt)
        ld.atStartOfDay(utc)
      }
    })

  /** This output is ISO 8601 compilant, e.g. 1999-01-01 */
  implicit val localDateStringIso: IsoString[LocalDate] = {
    IsoString.iso[LocalDate]( (ld: LocalDate) => {
        val datetimefmt = DateTimeFormatter.ISO_LOCAL_DATE
        ld.format(datetimefmt)
      },
      (s: String) => {
        val datetimefmt = DateTimeFormatter.ISO_LOCAL_DATE
        LocalDate.parse(s, datetimefmt)
      })
  }

  /** This output is ISO 8601 compilant, e.g. 1999-01-01T00:00:00.001 */
  implicit val localDateTimeStringIso: IsoString[LocalDateTime] = {
    IsoString.iso[LocalDateTime]( (ld: LocalDateTime) => {
        val datetimefmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ld.format(datetimefmt)
      },
      (s: String) => {
        val datetimefmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        LocalDateTime.parse(s, datetimefmt)
      })
  }
}
