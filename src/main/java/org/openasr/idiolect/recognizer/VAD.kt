package org.openasr.idiolect.recognizer

import kotlin.math.sqrt

class VAD(private var threshold: Short) {
    fun setThreshold(threshold: Short) {
        this.threshold = threshold
    }

    fun isSpeech(frame: ShortArray): Boolean {
        var sum = 0.0

        for (sample in frame) {
            sum += sample * sample
        }
        val rms = sqrt(sum / frame.size)

        return rms > threshold
    }
}
