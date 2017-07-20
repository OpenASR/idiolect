package org.openasr.idear.recognizer

import com.intellij.openapi.diagnostic.Logger
import java.io.IOException
import java.lang.Byte.*
import javax.sound.sampled.*

class AudioInputStreamWithAdjustableGain internal constructor(line: TargetDataLine) : AudioInputStream(line) {
    private val DEFAULT_MASTER_GAIN = 1.0
    private val DEFAULT_NOISE_LEVEL = 0.0
    private val logger = Logger.getInstance(AudioInputStreamWithAdjustableGain::class.java)
    private var masterGain = DEFAULT_MASTER_GAIN
    private var noiseLevel = DEFAULT_NOISE_LEVEL

    override fun read() = super.read()

    override fun read(b: ByteArray): Int {
        val read = super.read(b)

        dump(b, 0, b.size)

        for (i in 0 until read) b[i] = adjust(b[i])

        dump(b, 0, b.size)

        return read
    }

    private fun dump(b: ByteArray, off: Int, len: Int) {
        val sb = StringBuilder()
        for (i in off until off + len - 1) {
            sb.append(b[i].toInt())
            if (i != off + len - 1)
                sb.append(", ")
        }
        logger.info(sb.toString())
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = super.read(b, off, len)

        //dump(b, off, read);

        for (i in off until off + read) b[i] = adjust(b[i])

        //dump(b, off, read);

        return read
    }

    fun setMasterGain(mg: Double) = mg.let { masterGain = it }

    fun setNoiseLevel(nl: Double) = nl.let { noiseLevel = it }

    private fun adjust(b: Byte) = cut((b * masterGain).toByte())

    private fun cut(b: Byte) =
            if (b < MAX_VALUE * noiseLevel && b > MIN_VALUE * noiseLevel) 0 else b
}
