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

trait ThrowableFormats {
  this: PrimitiveFormats with StandardFormats with IsoFormats with CollectionFormats with AdditionalFormats =>

  private[this] type JF[A] = JsonFormat[A] // simple alias for reduced verbosity

  implicit lazy val throwableFormat: JF[Throwable] = throwableFormat0
  private[this] val throwableFormat0: JF[Throwable] = new ThrowableFormat

  implicit lazy val stackTraceElementFormat: JF[StackTraceElement] = stackTraceElementFormat0
  private[this] val stackTraceElementFormat0: JF[StackTraceElement] = new StackTraceElementFormat

  final class ThrowableFormat extends JF[Throwable] {
    def write[J](t: Throwable, builder: Builder[J]): Unit =
      {
        builder.beginObject()
        builder.addField("className", t.getClass.toString)
        builder.addField("message", Option(t.getMessage))
        builder.addField("cause", Option(t.getCause))
        builder.addField("stackTrace", t.getStackTrace.toVector)
        builder.endObject()
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): Throwable =
      jsOpt match {
        case Some(js) =>
          val size = unbuilder.beginObject(js)
          val className = unbuilder.readField[String]("className")
          val message = unbuilder.readField[Option[String]]("message")
          val cause = unbuilder.readField[Option[Throwable]]("cause")
          val stackTraces = unbuilder.readField[Vector[StackTraceElement]]("stackTrace")
          unbuilder.endObject()
          val t = new PersistedException(message.orNull, cause.orNull, className)
          t.setStackTrace(stackTraces.toArray)
          t
        case None => deserializationError("Expected JsObject, but got None")
      }
  }

  final class StackTraceElementFormat extends JF[StackTraceElement] {
    def write[J](a: StackTraceElement, builder: Builder[J]): Unit =
      {
        builder.beginObject()
        builder.addField("className", Option(a.getClassName))
        builder.addField("methodName", Option(a.getMethodName))
        builder.addField("fileName", Option(a.getFileName))
        builder.addField("lineNumber", Option(a.getLineNumber))
        builder.endObject()
      }
    def read[J](jsOpt: Option[J], unbuilder: Unbuilder[J]): StackTraceElement =
      jsOpt match {
        case Some(js) =>
          val size = unbuilder.beginObject(js)
          val className = unbuilder.readField[Option[String]]("className")
          val methodName = unbuilder.readField[Option[String]]("methodName")
          val fileName = unbuilder.readField[Option[String]]("fileName")
          val lineNumber = unbuilder.readField[Int]("lineNumber")
          unbuilder.endObject()
          new StackTraceElement(className.orNull, methodName.orNull,
            fileName.orNull, lineNumber)
        case None => deserializationError("Expected JsObject, but got None")
      }
  }
}

final class PersistedException(
  message: String,
  cause: Throwable,
  val className: String) extends Throwable(message, cause)
