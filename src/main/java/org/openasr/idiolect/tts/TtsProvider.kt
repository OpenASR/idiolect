package org.openasr.idiolect.tts

import org.openasr.idiolect.settings.ConfigurableExtension

interface TtsProvider : ConfigurableExtension {
    /**
     * Reads text aloud through the microphone.
     *
     * @param utterance Text to speak
     * @returns True if the text could be spoken, otherwise False
     */
    fun say(utterance: String)

    fun dispose()
}
