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

import java.util.UUID
import java.net.{ URI, URL }
import java.io.File
import java.math.{ BigInteger, BigDecimal => JBigDecimal }

trait JavaExtraFormats {
  this: PrimitiveFormats with AdditionalFormats with IsoFormats =>

  private[this] type JF[A] = JsonFormat[A] // simple alias for reduced verbosity

  implicit val javaBigIntegerFormat: JF[BigInteger] =
    project[BigInteger, BigInt](BigInt.apply, _.bigInteger)

  implicit val javaBigDecimalFormat: JF[JBigDecimal] =
    project[JBigDecimal, BigDecimal](BigDecimal.apply, _.bigDecimal)

  implicit val uuidStringIso: IsoString[UUID] = IsoString.iso[UUID](
    _.toString, UUID.fromString)
  implicit val uriStringIso: IsoString[URI] = IsoString.iso[URI](
    _.toASCIIString, new URI(_))
  implicit val urlStringIso: IsoString[URL] = IsoString.iso[URL](
    _.toURI.toASCIIString, (s: String) => (new URI(s)).toURL)

  implicit val fileStringIso: IsoString[File] = IsoString.iso[File](
    _.toURI.toASCIIString, (s: String) => new File(new URI(s)))
}
