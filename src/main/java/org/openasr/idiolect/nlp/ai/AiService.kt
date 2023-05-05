package org.openasr.idiolect.nlp.ai

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE
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
    fun setMaxTokens(maxTokens: Int) {
        client.maxTokens = maxTokens
    }
    fun setTemperature(temperature: Double) {
        client.temperature = temperature
    }
    fun setTopP(topP: Double) {
        client.topP = topP
    }
    
    fun listModels(type: OpenAiClient.ModelType): List<String> {
        return if (client.ensureConfigured()) {
            client.listModels(type)
        }
        else listOf()
    }

    fun sendCompletion(prompt: String) {
        if (!client.ensureConfigured()) return

        val publisher = messageBus.syncPublisher(AiResponseListener.AI_RESPONSE_TOPIC)

        publisher.onUserPrompt(prompt)

//        val choices = client.sendCompletion(prompt)
        val choices = listOf("""
            Here is the code for `AiService`:
            ```
            class AiService {
                init {
                    println("hello world")
                }
            }
            ```
            """.trimIndent())

        publisher.onAiResponse(choices)
    }

    fun sendChat(systemMessage: String, prompt: String) {
        if (!client.ensureConfigured()) return

        val publisher = messageBus.syncPublisher(AiResponseListener.AI_RESPONSE_TOPIC)

        publisher.onUserPrompt(prompt)

        val choices = client.sendChat(systemMessage, prompt)

        publisher.onAiResponse(choices)
    }

    fun sendChatWithContext(systemMessage: String, prompt: String, context: String) {
        if (!client.ensureConfigured()) return

        val publisher = messageBus.syncPublisher(AiResponseListener.AI_RESPONSE_TOPIC)

        publisher.onUserPrompt(prompt)

        val choices = client.sendChatWithContext(systemMessage, prompt, context)

        publisher.onAiResponse(choices)
    }

//    fun sendChatMessages(messages: List<ChatMessage>) {
//          if (!client.ensureConfigured()) return
//
//        val publisher = messageBus.syncPublisher(AiResponseListener.AI_RESPONSE_TOPIC)
//
//        publisher.onUserPrompt(prompt)
//
//        val choices = client.sendChatMessages(systemMessage, prompt, context)
//
//        publisher.onAiResponse(choices)
//    }
}
