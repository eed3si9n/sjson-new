/*
 * Copyright (C) 2023 Eugene Yokota
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

import java.io.File
import java.net.{ URI, URL }
import java.util.Locale

trait IsoStringLong[A] {
  def to(a: A): (String, Long)
  def from(p: (String, Long)): A
}

object IsoStringLong {
  def iso[A](to0: A => (String, Long), from0: ((String, Long)) => A): IsoStringLong[A] = new IsoStringLong[A] {
    def to(a: A): (String, Long) = to0(a)
    def from(p: (String, Long)): A = from0(p)
  }

  private[sjsonnew] lazy val isWindows: Boolean =
    System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")

  private[sjsonnew] final val FileScheme = "file"

  private[sjsonnew] def fileToString(file: File): String = {
    val p = file.getPath
    if (p.startsWith(File.separatorChar.toString) && isWindows) {
      if (p.startsWith("""\\""")) {
        // supports \\laptop\My Documents\Some.doc on Windows
        new URI(FileScheme, normalizeName(p), null).toASCIIString
      }
      else {
        // supports /tmp on Windows
        new URI(FileScheme, "", normalizeName(p), null).toASCIIString
      }
    } else if (file.isAbsolute) {
      //not using f.toURI to avoid filesystem syscalls
      //we use empty string as host to force file:// instead of just file:
      new URI(FileScheme, "", normalizeName(ensureHeadSlash(file.getAbsolutePath)), null).toASCIIString
    } else {
      new URI(null, normalizeName(file.getPath), null).toASCIIString
    }
  }

  private[this] def ensureHeadSlash(name: String) = {
    if(name.nonEmpty && name.head != File.separatorChar) s"${File.separatorChar}$name"
    else name
  }
  private[this] def normalizeName(name: String) = {
    val sep = File.separatorChar
    if (sep == '/') name else name.replace(sep, '/')
  }

  private[sjsonnew] def uriToFile(uri: URI): File = {
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
}
