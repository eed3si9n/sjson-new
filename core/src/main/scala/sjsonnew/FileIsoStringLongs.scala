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

trait FileIsoStringLongs {
  implicit lazy val fileStringLongIso: IsoStringLong[File] = IsoStringLong.iso[File](
    (file: File) => (IsoStringLong.fileToString(file), HashUtil.farmHash(file.toPath())),
    (p: (String, Long)) => IsoStringLong.uriToFile(new URI(p._1)))

  implicit lazy val pathStringLongIso: IsoStringLong[Path] = IsoStringLong.iso[Path](
    (file: Path) => (file.toString, HashUtil.farmHash(file)),
    (p: (String, Long)) => Paths.get(p._1))

  implicit lazy val fileJsonKeyFormat: JsonKeyFormat[File] = 
    JsonKeyFormat(IsoStringLong.fileToString, key => IsoStringLong.uriToFile(new URI(key)))

  implicit lazy val pathJsonKeyFormat: JsonKeyFormat[Path] = 
    JsonKeyFormat(_.toString, Paths.get(_))
}
