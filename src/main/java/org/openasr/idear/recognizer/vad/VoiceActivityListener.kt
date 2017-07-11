package org.openasr.idear.recognizer.vad

import java.io.InputStream

interface VoiceActivityListener {
    fun onVoiceActivity(voiceStream: InputStream)
}