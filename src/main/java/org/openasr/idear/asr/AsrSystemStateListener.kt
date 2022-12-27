package org.openasr.idear.asr

import com.intellij.util.messages.Topic

interface AsrSystemStateListener {
    companion object {
        @Topic.AppLevel
        val ASR_STATE_TOPIC = Topic.create("ASR System State", AsrSystemStateListener::class.java)
    }


    fun onAsrStatus(message: String) {}

    fun onAsrReady(message: String) {}
}
