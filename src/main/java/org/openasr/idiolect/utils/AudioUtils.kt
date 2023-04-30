package org.openasr.idiolect.utils

import javax.sound.sampled.*


object AudioUtils {
    fun getAudioInputDevices(): List<Mixer.Info> {
        return AudioSystem.getMixerInfo().filter(::isInputDevice)
    }

    private fun isInputDevice(mixerInfo: Mixer.Info): Boolean {
        return AudioSystem.getMixer(mixerInfo).targetLineInfo.any { it.lineClass == TargetDataLine::class.java }
    }

    fun readLittleEndianShorts(b: ByteArray, numberOfBytes: Int, callback: (sample: Short) -> Unit) {
        for (i in 0 until numberOfBytes step 2) {
            val sample = ((b[i + 1].toInt() shl 8) or (b[i].toInt() and 0x00FF)).toShort()
            callback(sample)
        }
    }

    fun writeLittleEndianShort(b: ByteArray, numberOfBytes: Int, callback: () -> Int) {
        for (i in 0 until numberOfBytes step 2) {
            val sample = callback()
            b[i] = (sample and 0xff).toByte()
            b[i + 1] = ((sample shr 8) and 0xff).toByte()
        }
    }
}
