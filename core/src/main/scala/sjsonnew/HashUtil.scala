package sjsonnew

import java.io.{ BufferedInputStream, File, FileInputStream, FileNotFoundException, InputStream }
import java.nio.file.{ Files, Path }
import net.openhft.hashing.LongHashFunction

object HashUtil {
  // https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/hash/MurmurHash.java
  def hashLong(data: Long): Int =
    {
      val m: Int = 0x5bd1e995
      val r: Int = 24
      var h: Int = 0
      var k: Int = (data * m).toInt
      k ^= k >>> r
      h ^= k * m
      k = ((data >> 32) * m).toInt
      k ^= k >>> r
      h *= m
      h ^= k * m
      h ^= h >>> 13
      h *= m
      h ^= h >>> 15
      h
    }

  private[sjsonnew] def farmHash(bytes: Array[Byte]): Long =
    LongHashFunction.farmNa().hashBytes(bytes)

  private[sjsonnew] def farmHash(path: Path): Long = {
    // allocating many byte arrays for large files may lead to OOME
    // but it is more efficient for small files
    val largeFileLimit = 10 * 1024 * 1024
    if (!Files.exists(path) || Files.isDirectory(path)) 0L
    else if (Files.size(path) < largeFileLimit) farmHash(Files.readAllBytes(path))
    else farmHash(sha256(path.toFile))
  }

  /** Calculates the SHA-1 hash of the given file. */
  def sha256(file: File): Array[Byte] =
    try sha256(new BufferedInputStream(new FileInputStream(file))) // apply closes the stream
    catch { case _: FileNotFoundException => Array() }

  /** Calculates the SHA-1 hash of the given stream, closing it when finished. */
  def sha256(stream: InputStream): Array[Byte] = {
    val BufferSize = 8192
    import java.security.{ DigestInputStream, MessageDigest }
    val digest = MessageDigest.getInstance("SHA-256")
    try {
      val dis = new DigestInputStream(stream, digest)
      val buffer = new Array[Byte](BufferSize)
      while (dis.read(buffer) >= 0) {}
      dis.close()
      digest.digest
    } finally {
      stream.close()
    }
  }
}
