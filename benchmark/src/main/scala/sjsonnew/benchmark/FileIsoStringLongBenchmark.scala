package sjsonnew.benchmark

import java.io.File

import org.openjdk.jmh.annotations.Benchmark
import sjsonnew.IsoStringLong

class FileIsoStringLongBenchmark {

  @Benchmark
  def fileB: String = {
    val isoFile = implicitly[IsoStringLong[File]]
    val f = new File("/tmp")
    isoFile.to(f)
  }
}
