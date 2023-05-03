package org.openasr.idiolect.nlp.ai

//import com.aallam.openai.api.completion.CompletionRequest
//import com.aallam.openai.api.model.ModelId
//import com.aallam.openai.client.OpenAI
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
            initialiseModelLists()
        }
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
        return sendChat(listOf(
            ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage),
            ChatMessage(ChatMessageRole.USER.value(), prompt)
        ))
    }

    fun sendChat(systemMessage: String, prompt: String, context: String): List<String> {
        return sendChat(listOf(
            ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage),
            ChatMessage(ChatMessageRole.ASSISTANT.value(), context),
            ChatMessage(ChatMessageRole.USER.value(), prompt)
        ))
    }

    fun sendChat(messages: List<ChatMessage>): List<String> {
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

//        completion.choices.forEach { choice -> logger.info(choice.text) }
//        return NlpResponse("Chat", mapOf("choice" to (completion.choices.first().text)))



//        val chatCompletionRequest = ChatCompletionRequest(
//            model = ModelId("gpt-3.5-turbo"),
//            messages = listOf(
//
//            )
//        )
//
//        val completion = runBlocking { openAI?.chatCompletion(completionRequest) }
//
//        if (completion == null || completion.choices.isEmpty()) {
//            return null
//        }
//        completion.choices.forEach { choice -> logger.info(choice.message?.content) }
//        return NlpResponse("Chat", mapOf("choice" to (completion.choices.first().message?.content ?: "")))

        // 53 tokens for a couple of words, or 168 for "example of a for loop in kotlin"
        // Sure! Here's an example of a for loop in Kotlin:
        //
        //```
        //fun main() {
        //    val numbers = listOf(1, 2, 3, 4, 5)
        //
        //    for (num in numbers) {
        //        println(num)
        //    }
        //}
        //```
        //
        //In this example, we have a list called `numbers` that contains the values 1 through 5. We use a `for` loop to iterate over each value in the list and print it to the console. The loop variable `num` takes on the value of each element in the list during each iteration. The output of this code would be:
        //
        //```
        //1
        //2
        //3
        //4
        //5
        //```
}
