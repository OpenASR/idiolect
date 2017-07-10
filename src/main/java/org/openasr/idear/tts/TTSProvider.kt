package org.openasr.idear.tts

interface TTSProvider {
    fun say(text: String?)
    fun dispose()
}