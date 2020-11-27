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

import java.util.{ UUID, Optional }
import java.net.{ URI, URL }
import java.io.File
import java.math.{ BigInteger, BigDecimal => JBigDecimal }

trait JavaExtraFormats {
  this: PrimitiveFormats with AdditionalFormats with IsoFormats =>

  private[this] type JF[A] = JsonFormat[A] // simple alias for reduced verbosity

  implicit val javaBigIntegerFormat: JF[BigInteger] =
    projectFormat[BigInteger, BigInt](BigInt.apply, _.bigInteger)

  implicit val javaBigDecimalFormat: JF[JBigDecimal] =
    projectFormat[JBigDecimal, BigDecimal](BigDecimal.apply, _.bigDecimal)

  implicit val uuidStringIso: IsoString[UUID] = IsoString.iso[UUID](
    _.toString, UUID.fromString)
  implicit val uriStringIso: IsoString[URI] = IsoString.iso[URI](
    _.toASCIIString, new URI(_))
  implicit val urlStringIso: IsoString[URL] = IsoString.iso[URL](
    _.toURI.toASCIIString, (s: String) => (new URI(s)).toURL)

  private[this] final val FileScheme = "file"

  implicit val fileStringIso: IsoString[File] = IsoString.iso[File](
    (f: File) => {
      val p = f.getPath
      if (f.isAbsolute) {
        //not using f.toURI to avoid filesystem syscalls
        //we use empty string as host to force file:// instead of just file:
        new URI(FileScheme, "", normalizeName(slashify(f.getAbsolutePath)), null).toASCIIString
      } else if (p.startsWith(File.separatorChar.toString)) {
        // supports /tmp on Windows
        new URI(FileScheme, "", normalizeName(slashify(p)), null).toASCIIString
      } else {
        new URI(null, normalizeName(f.getPath), null).toASCIIString
      }
    },
    (s: String) => uriToFile(new URI(s)))

  private[this] def slashify(name: String) = {
    if(name.nonEmpty && name.head != File.separatorChar) File.separatorChar + name
    else name
  }
  private[this] def normalizeName(name: String) = {
    val sep = File.separatorChar
    if (sep == '/') name else name.replace(sep, '/')
  }

  private[this] def uriToFile(uri: URI): File = {
    val part = uri.getSchemeSpecificPart
    // scheme might be omitted for relative URI reference.
    assert(
      Option(uri.getScheme) match {
        case None | Some(FileScheme) => true
        case _                       => false
      },
      s"Expected protocol to be '$FileScheme' or empty in URI $uri"
    )
    Option(uri.getAuthority) match {
      case None if part startsWith "/" => new File(uri)
      case _                           =>
        if (!(part startsWith "/") && (part contains ":")) new File("//" + part)
        else new File(part)
    }
  }

  implicit def optionalFormat[A :JF]: JF[Optional[A]] = new OptionalFormat[A]
  final class OptionalFormat[A :JF] extends JF[Optional[A]] {
    lazy val elemFormat = implicitly[JF[A]]
    def write[J](o: Optional[A], builder: Builder[J]): Unit =
      if (o.isPresent) elemFormat.write(o.get, builder)
      else builder.writeNull
    override def addField[J](name: String, o: Optional[A], builder: Builder[J]): Unit =
      if (o.isPresent) {
        builder.addFieldName(name)
        write(o, builder)
      } else ()
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Optional[A] =
      jsOpt match {
        case Some(js) =>
          if (unbuilder.isJnull(js)) Optional.empty[A]
          else Optional.ofNullable(elemFormat.read(jsOpt, unbuilder))
        case None => Optional.empty[A]
      }
  }
}
