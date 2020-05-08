package org.openasr.idear.asr.picovoice

interface AudioConsumer {
    fun consume(pcm: ShortArray?)

    fun getFrameLength(): Int
    fun getSampleRate(): Int
}