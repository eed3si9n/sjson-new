package sjsonnew

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
}
