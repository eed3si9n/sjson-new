package sjsonnew
package benchmark

import org.openjdk.jmh.annotations._
import java.util.concurrent.TimeUnit

import sbt.librarymanagement.ModuleID
import sbt.internal.librarymanagement.impl.DependencyBuilders
import java.io.{File, FileInputStream, FileOutputStream}

import sbt.io.{IO, Using}
import sbt.io.syntax._

import scala.util.Random

@State(Scope.Benchmark)
abstract class JsonBenchmark[J](converter: SupportConverter[J]) {
  var _modules: Vector[ModuleID] = _

  @Setup(Level.Trial)
  def setUpData(): Unit = {
    _modules = BenchmarkData.moduleIds
  }

  @TearDown(Level.Trial)
  def tearDown(): Unit = {
    _modules = null
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def moduleId1SaveToFile: Unit = {
    import LibraryManagementProtocol._
    val js = converter.toJson(_modules)
    saveToFile(js.get, testFile)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def moduleId2LoadFromFile: Unit = {
    import LibraryManagementProtocol._
    val js = loadFromFile(testFile)
    converter.fromJson[Vector[ModuleID]](js)
  }

  def saveToFile(js: J, f: File): Unit
  def loadFromFile(f: File): J
  def testFile: File
}

object BenchmarkData extends DependencyBuilders {
  lazy val moduleIds = listOfModuleIds(200000)
  def listOfModuleIds(n: Int): Vector[ModuleID] =
    (1 to n).toVector map { x =>
      randomOrg % randomName % randomVersion
    }
  private val rand = new Random(1L)
  def randomOrg: String = Random.nextString(15)
  def randomName: String = Random.nextString(10)
  def randomVersion: String =
    s"${rand.nextInt % 10}.${rand.nextInt % 10}.${rand.nextInt % 10}"
}

class SprayBenchmark
    extends JsonBenchmark[spray.json.JsValue](sjsonnew.support.spray.Converter) {
  import spray.json._
  lazy val testFile: File = file("target") / "test-spray.json"
  def saveToFile(js: JsValue, f: File): Unit =
    IO.write(f, CompactPrinter(js), IO.utf8)
  def loadFromFile(f: File): JsValue =
    jawn.support.spray.Parser.parseFromFile(f).get
}

class GzipSprayBenchmark
    extends JsonBenchmark[spray.json.JsValue](sjsonnew.support.spray.Converter) {
  import java.io.{OutputStreamWriter, StringWriter}
  import spray.json._
  lazy val testFile: File = file("target") / "test-spray.json.gz"
  def saveToFile(js: JsValue, f: File): Unit =
    Using.fileOutputStream(false)(f) { out =>
      Using.gzipOutputStream(out) { gz =>
        val w = new OutputStreamWriter(gz, "UTF-8")
        try {
          val s = CompactPrinter(js)
          w.write(s)
        } finally {
          w.close()
        }
      }
    }
  def loadFromFile(f: File): JsValue =
    Using.fileInputStream(f) { in =>
      Using.gzipInputStream(in) { gz =>
        Using.streamReader(gz, IO.utf8) { r =>
          val writer = new StringWriter
          val buffer = new Array[Char](10240)
          var length = r.read(buffer)
          while (length > 0) {
            writer.write(buffer, 0, length)
            length = r.read(buffer)
          }
          jawn.support.spray.Parser.parseFromString(writer.toString).get
        }
      }
    }
}

class ScalaJsonBenchmark
    extends JsonBenchmark[shaded.scalajson.ast.unsafe.JValue](
      sjsonnew.support.scalajson.unsafe.Converter) {
  import shaded.scalajson.ast.unsafe._
  import sjsonnew.support.scalajson.unsafe.{CompactPrinter, Parser}
  lazy val testFile: File = file("target") / "test-scalajson.json"
  def saveToFile(js: JValue, f: File): Unit =
    IO.write(f, CompactPrinter(js), IO.utf8)
  def loadFromFile(f: File): JValue =
    Parser.parseFromFile(f).get
}

class GzipScalaJsonBenchmark
    extends JsonBenchmark[shaded.scalajson.ast.unsafe.JValue](
      sjsonnew.support.scalajson.unsafe.Converter) {
  import java.io.{OutputStreamWriter, StringWriter}
  import shaded.scalajson.ast.unsafe._
  import sjsonnew.support.scalajson.unsafe.{CompactPrinter, Parser}
  lazy val testFile: File = file("target") / "test-scalajson.json.gz"
  def saveToFile(js: JValue, f: File): Unit =
    Using.fileOutputStream(false)(f) { out =>
      Using.gzipOutputStream(out) { gz =>
        val w = new OutputStreamWriter(gz, "UTF-8")
        try {
          val s = CompactPrinter(js)
          w.write(s)
        } finally {
          w.close()
        }
      }
    }
  def loadFromFile(f: File): JValue =
    Using.fileInputStream(f) { in =>
      Using.gzipInputStream(in) { gz =>
        Using.streamReader(gz, IO.utf8) { r =>
          val writer = new StringWriter
          val buffer = new Array[Char](10240)
          var length = r.read(buffer)
          while (length > 0) {
            writer.write(buffer, 0, length)
            length = r.read(buffer)
          }
          Parser.parseFromString(writer.toString).get
        }
      }
    }
}

class MessagePackBenchmark
    extends JsonBenchmark[org.msgpack.value.Value](
      sjsonnew.support.msgpack.Converter) {
  import org.msgpack.core.MessagePack
  import org.msgpack.value.Value
  lazy val testFile: File = file("target") / "test-msgpack.bin"
  def saveToFile(js: Value, f: File): Unit = {
    val packer = MessagePack.newDefaultPacker(new FileOutputStream(f))
    packer.packValue(js)
    packer.flush()
  }
  def loadFromFile(f: File): Value = {
    val unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(f))
    val value = unpacker.unpackValue()
    value
  }
}
