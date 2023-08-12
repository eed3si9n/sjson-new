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
package support.spray

import spray.json.{ JsValue, JsNumber, JsString, JsNull, JsTrue, JsFalse, JsObject }
import java.io.File

object PathOnlyFormatsSpec extends verify.BasicTestSuite {
  import IsoStringLong.isWindows
  import PathOnlyFormats._

  test("The PathOnlyFormats can override the default") {
    val f = new File("/tmp")
    val f2 = new File(new File("src"), "main")
    // "convert a File to JsString" in {
    Predef.assert(Converter.toJsonUnsafe(f) == JsString("file:///tmp"))

    // "convert a relative path to JsString" in {
    // https://tools.ietf.org/html/rfc3986#section-4.2
    Predef.assert(Converter.toJsonUnsafe(f2) == JsString("src/main"))

    // "convert the JsString back to the File" in {
    Predef.assert(Converter.fromJsonUnsafe[File](JsString("file:///tmp")) == f)

    // "convert the JsString back to the relative path" in {
    Predef.assert(Converter.fromJsonUnsafe[File](JsString("src/main")) == f2)

    // "convert an absolute path on Windows" in {
    if (isWindows) Predef.assert(Converter.toJsonUnsafe(new File("""C:\Documents and Settings\""")) == JsString("file:///C:/Documents%20and%20Settings"))
    else ()

    // "convert a relative path on Windows" in {
    if (isWindows) Predef.assert(Converter.toJsonUnsafe(new File("""..\My Documents\test""")) == JsString("../My%20Documents/test"))
    else ()

    // "convert a UNC path on Windows" in {
    if (isWindows) Predef.assert(Converter.toJsonUnsafe(new File("""\\laptop\My Documents\Some.doc""")) == JsString("file://laptop/My%20Documents/Some.doc"))
    else ()
  }
}
