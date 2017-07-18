package org.openasr.idear.tts

interface TTSProvider {
    /**
     * Reads text aloud through the microphone.
     *
     * @param utterance Text to speak
     * @returns True if the text could be spoken, otherwise False
     */
    fun say(utterance: String): Boolean

    fun dispose()
}