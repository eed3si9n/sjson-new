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
import java.net.URI
import java.nio.file.{ Path, Paths }

trait PathOnlyFormats {
  implicit val pathOnlyFileFormat: JsonFormat[File] = new JsonFormat[File] {
    def write[J](file: File, builder: Builder[J]): Unit =
      builder.writeString(IsoStringLong.fileToString(file))
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): File =
      jsOpt match {
        case Some(js) => IsoStringLong.uriToFile(new URI(unbuilder.readString(js)))
        case None     => deserializationError(s"Expected JsString but got None")
      }
  }

  implicit val pathOnlyPathFormat: JsonFormat[Path] = new JsonFormat[Path] {
    def write[J](file: Path, builder: Builder[J]): Unit =
      builder.writeString(file.toString)
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Path =
      jsOpt match {
        case Some(js) => Paths.get(unbuilder.readString(js))
        case None     => deserializationError(s"Expected JsString but got None")
      }
  }
}

object PathOnlyFormats extends PathOnlyFormats
