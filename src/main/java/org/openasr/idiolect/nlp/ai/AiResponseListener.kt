package org.openasr.idiolect.nlp.ai

import com.intellij.util.messages.Topic

interface AiResponseListener {
    companion object {
        @Topic.AppLevel
        val AI_RESPONSE_TOPIC = Topic.create("AI Response", AiResponseListener::class.java)
    }

    fun onUserPrompt(prompt: String)

    fun onAiResponse(choices: List<String>)
}
