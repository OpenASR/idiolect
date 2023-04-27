package org.openasr.idiolect.recognizer.filter

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.sound.sampled.AudioInputStream

interface Filter {
    companion object {
        fun calculateHzPerSample(sampleRate: Float, sampleSizeInBits: Int, channels: Int = 1) =
            (sampleRate / (sampleSizeInBits / 8) / channels).toInt()
    }
    fun filterSample(sample: Int): Int

    /** Used to filter live audio */
    fun filterSample(b: ByteArray, offset: Int) {
        val sample = ((b[offset + 1].toInt() shl 8) + (b[offset].toInt() and 0xff))

        val filteredSample = filterSample(sample)

        b[offset] = (filteredSample and 0xff).toByte()
        b[offset + 1] = ((filteredSample shr 8) and 0xff).toByte()
    }

    /** Can be used to record the output of the Filters for testing */
    fun filter(audioInputStream: InputStream): ByteArrayOutputStream {
        val end = audioInputStream.available()
        val output = ByteArrayOutputStream(end)
        var b = ByteArray(2)

        for (i in 0 until end step 2) {
            audioInputStream.readNBytes(2) // read(b)

            filterSample(b, 0)

            output.writeBytes(b)
        }

        return output
    }
}
