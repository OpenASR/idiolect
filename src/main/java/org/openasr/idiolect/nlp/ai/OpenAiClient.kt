package org.openasr.idiolect.nlp.ai

//import com.aallam.openai.api.completion.CompletionRequest
//import com.aallam.openai.api.model.ModelId
//import com.aallam.openai.client.OpenAI
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.invokeLater
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import com.intellij.openapi.diagnostic.logger
import com.theokanning.openai.completion.CompletionRequest
import kotlinx.coroutines.runBlocking


class OpenAiClient(private var apiKey: String?) {
    private val log = logger<OpenAiClient>()
    private var openAi: OpenAiService? = null
    var chatModel = "gpt-3.5-turbo"
    var completionModel = "text-davinci-003"
    var maxTokens: Int? = null // = 16 // default set by OpenAI. max: 2048 or 4096
    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     */
    var temperature: Double? = null // = 1.0
    var topP: Double? = null // = 1.0

    enum class ModelType {
        completions,
        chat,
        edits,
        transcriptions,
    }

    private val modelsByType = mutableMapOf<ModelType, List<String>>(
        ModelType.completions to listOf(),
        ModelType.chat to listOf(),
        ModelType.edits to listOf(),
        ModelType.transcriptions to listOf()
    )

    init {
        apiKey?.let {
            openAi = OpenAiService(it)

//            invokeLater {
//                initialiseModelLists()
//            }
        }
    }

    fun ensureConfigured(): Boolean {
        if (apiKey == null) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Idiolect")
                .createNotification(
                    "OpenAI not configured",
                    """
                    <p>Create and configure OpenAI API key<p>
                    <p><a href="https://platform.openai.com/account/api-keys">https://platform.openai.com/account/api-keys</a></p>
                """.trimIndent(), NotificationType.INFORMATION
                )
                .notify(null)
            return false
        }

        return true
    }

    fun setApiKey(apiKey: String?) {
        if (apiKey.isNullOrEmpty()) {
            openAi = null
        } else if (apiKey != this.apiKey) {
            this.apiKey = apiKey
            openAi = OpenAiService(apiKey)
            initialiseModelLists()
        }
    }



//    @OptIn(BetaOpenAI::class)
    fun sendCompletion(prompt: String): List<String> {
        return openAi?.let {
            val completionRequest = CompletionRequest.builder()
                .model(completionModel)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .topP(topP)
                .prompt(prompt)
                .build()

            val completion = runBlocking { it.createCompletion(completionRequest) }
            log.info("OpenAI completion request cost ${completion.usage?.totalTokens} tokens")

            return completion.choices.map { choice ->
                log.debug("  finish reason: ${choice.finish_reason}")
                choice.text
            }
        } ?: emptyList()
    }

    fun sendChat(systemMessage: String, prompt: String): List<String> {
        return sendChatMessages(listOf(
            ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage),
            ChatMessage(ChatMessageRole.USER.value(), prompt)
        ))
    }

    fun sendChatWithContext(systemMessage: String, prompt: String, context: String): List<String> {
        return sendChatMessages(listOf(
            ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage),
            ChatMessage(ChatMessageRole.ASSISTANT.value(), context),
            ChatMessage(ChatMessageRole.USER.value(), prompt)
        ))
    }

    fun sendChatMessages(messages: List<ChatMessage>): List<String> {
        return openAi?.let {
            val completionRequest = ChatCompletionRequest.builder()
                .model(chatModel)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .topP(topP)
                .messages(messages)
                .build()

            val completion = runBlocking { it.createChatCompletion(completionRequest) }
            log.info("OpenAI chat request cost ${completion.usage?.totalTokens} tokens")

            return completion.choices.map { choice ->
                log.debug("  finish reason: ${choice.finishReason}")
                choice.message.content
            }
        } ?: emptyList()
    }


    fun listModels(type: ModelType): List<String> {
        return modelsByType[type]!!
    }

    private fun initialiseModelLists() {
        openAi?.let {openAi ->
            val completionsRegex = Regex("^text(?!.*(?:edit|embedding|moderation|search|similarity|:)).*\$")
            val models = openAi.listModels().map { model -> model.id }

            modelsByType[ModelType.chat] = models.filter { it.startsWith("gpt-") }.sortedDescending()
            modelsByType[ModelType.completions] = models.filter { it.matches(completionsRegex) }.sortedDescending()
            modelsByType[ModelType.edits] = models.filter { it.contains("edit") }.sortedDescending()
            modelsByType[ModelType.transcriptions] = models.filter { it.contains("whisper") }.sortedDescending()
        }
    }
}
