package org.openasr.idiolect.utils

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine

object AudioUtils {
    fun getAudioInputDevices(): List<Mixer.Info> {
        return AudioSystem.getMixerInfo().filter(::isInputDevice)
    }

    private fun isInputDevice(mixerInfo: Mixer.Info): Boolean {
        return AudioSystem.getMixer(mixerInfo).targetLineInfo.any { it.lineClass == TargetDataLine::class.java }
    }

    fun readLittleEndianShorts(b: ByteArray, bytes: Int, callback: (sample: Short) -> Unit) {
        for (i in 0 until bytes step 2) {
            val sample = ((b[i + 1].toInt() shl 8) or (b[i].toInt() and 0x00FF)).toShort()
            callback(sample)
        }
    }
}
