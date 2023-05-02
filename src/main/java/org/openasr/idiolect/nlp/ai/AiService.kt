package org.openasr.idiolect.nlp.ai

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import org.openasr.idiolect.settings.openai.OpenAiConfig

@Service
class AiService {
    companion object {
        private val messageBus = ApplicationManager.getApplication().messageBus
    }

    private var client = OpenAiClient(OpenAiConfig.apiKey)

    fun setApiKey(apiKey: String) = client.setApiKey(apiKey)
    fun setCompletionModel(model: String) {
        client.completionModel = model
    }
    fun setChatModel(model: String) {
        client.chatModel = model
    }

    fun sendCompletion(prompt: String) {
        val publisher = messageBus.syncPublisher(AiResponseListener.AI_RESPONSE_TOPIC)

        publisher.onUserPrompt(prompt)

        val choices = client.sendCompletion(prompt)

        publisher.onAiResponse(choices)
    }
}
