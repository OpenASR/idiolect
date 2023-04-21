package org.openasr.idiolect.utils

object AudioUtils {
    fun readLittleEndianShorts(b: ByteArray, bytes: Int, callback: (sample: Short) -> Unit) {
        for (i in 0 until bytes step 2) {
            val sample = ((b[i + 1].toInt() shl 8) or (b[i].toInt() and 0x00FF)).toShort()
            callback(sample)
        }
    }
}
