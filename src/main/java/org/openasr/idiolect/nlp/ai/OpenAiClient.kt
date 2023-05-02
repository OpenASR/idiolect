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
//    private var apiKey: String? = null

    init {
        apiKey?.let {
            openAi = OpenAiService(it)
        }
    }

    fun setApiKey(apiKey: String?) {
        if (apiKey.isNullOrEmpty()) {
            openAi = null
        } else if (apiKey != this.apiKey) {
            this.apiKey = apiKey
            openAi = OpenAiService(apiKey)
        }
    }

//    @OptIn(BetaOpenAI::class)
    fun sendCompletion(prompt: String): List<String> {
        return openAi?.let {
            val completionRequest = CompletionRequest.builder()
                .model(completionModel)
                .maxTokens(2048)
                .prompt(prompt)
                .build()

            val completion = runBlocking { it.createCompletion(completionRequest) }
            log.info("OpenAI request cost ${completion.usage?.totalTokens} tokens")

            return completion.choices.map { choice ->
                log.debug("  finish reason: ${choice.finish_reason}")
                choice.text
            }
        } ?: emptyList()
    }

//        completion.choices.forEach { choice -> logger.info(choice.text) }
//        return NlpResponse("Chat", mapOf("choice" to (completion.choices.first().text)))



//        val chatCompletionRequest = ChatCompletionRequest(
//            model = ModelId("gpt-3.5-turbo"),
//            messages = listOf(
//                ChatMessage(ChatRole.System, "You are a helpful pair programming assistant"),
//                ChatMessage(ChatRole.Assistant, context.toString()),
//                ChatMessage(ChatRole.User, nlpRequest.utterance)
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
