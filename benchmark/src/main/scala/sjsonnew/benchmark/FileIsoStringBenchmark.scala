package sjsonnew.benchmark

import java.io.File

import org.openjdk.jmh.annotations.Benchmark
import sjsonnew.IsoString

class FileIsoStringBenchmark {

  @Benchmark
  def fileB: String = {
    import sjsonnew.BasicJsonProtocol._
    val isoFile = implicitly[IsoString[File]]
    val f = new File("/tmp")
    isoFile.to(f)
  }
}
