package org.openasr.idiolect.recognizer

import java.io.IOException
import java.lang.Byte.*
import javax.sound.sampled.*

class AudioInputStreamWithAdjustableGain internal constructor(line: TargetDataLine) : AudioInputStream(line) {
    private val DEFAULT_MASTER_GAIN = 1.0
    private val DEFAULT_NOISE_LEVEL = 0.0
    private val MAX_SAMPLE_VALUE = 32768.0
    private var masterGain = DEFAULT_MASTER_GAIN
    private var noiseLevel = DEFAULT_NOISE_LEVEL

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val bytesRead = super.read(b, off, len)
        if (bytesRead == -1) {
            return bytesRead
        }

        val end = off + bytesRead

        // step by 2 bytes because `sampleSizeInBytes = format.sampleSizeInBits / 8` = 2
        for (i in off until end step 2) {
            // TODO: delegate to Filter.filterSample(b, i) and move into recognizer.filter
            var sample = ((b[i + 1].toInt() shl 8) + (b[i].toInt() and 0xff)) // / MAX_SAMPLE_VALUE

            val amplifiedSample = transformSample(sample)

            b[i] = (amplifiedSample and 0xff).toByte()
            b[i + 1] = ((amplifiedSample shr 8) and 0xff).toByte()
        }

        //dump(b, off, read);

        return bytesRead
    }

    fun transformSample(sample: Int): Int {
        if (sample < noiseLevel && sample > -noiseLevel) {
            return 0
        }
        return (sample * masterGain).toInt().coerceIn(-32768, 32767)
    }

    fun setMasterGain(mg: Double) = mg.let { masterGain = it }

    fun setNoiseLevel(nl: Double) = nl.let { noiseLevel = it }

    private fun dump(b: ByteArray, off: Int, len: Int) {
        val sb = StringBuilder()
        for (i in off until off + len - 1) {
            sb.append(b[i].toInt())
            if (i != off + len - 1)
                sb.append(", ")
        }
    }

    private fun adjust(b: Byte) {
        cut((b * masterGain).toInt().toByte())
    }

    private fun cut(b: Byte) =
        if (b < MAX_VALUE * noiseLevel && b > MIN_VALUE * noiseLevel) 0 else b
}
