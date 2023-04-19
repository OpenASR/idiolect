package org.openasr.idiolect.actions.recognition

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component
import org.openasr.idiolect.settings.openai.OpenAiConfig

class OpenAiRecognizer : IntentResolver("OpenAI", Int.MAX_VALUE) {
    private val logger = Logger.getInstance(javaClass)

    override val grammars = listOf(NlpGrammar("OpenAI").withExample("chat gpt"))
    private var service: OpenAiService? = null

    init {
//        service = OpenAiService(OpenAiConfig.apiKey)
    }

    override fun isSupported(context: NlpContext, component: Component?): Boolean {
        if (service == null) {
            if (OpenAiConfig.apiKey.isNullOrEmpty()) {
//            showNotificationForApiKey()
                return false
            } else {
                // TODO - subscribe for changes
                service = OpenAiService(OpenAiConfig.apiKey)
            }
        }
        return true
    }

    override fun tryResolveIntent(nlpRequest: NlpRequest, context: NlpContext): NlpResponse? {
        val completionRequest = ChatCompletionRequest.builder()
            .messages(listOf(
                ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a helpful pair programming assistant"),
//                ChatMessage(ChatMessageRole.ASSISTANT.value(), context),
                ChatMessage(ChatMessageRole.USER.value(), nlpRequest.utterance)
            ))
            .model(OpenAiConfig.settings.model)
            .build()
        val completion = service!!.createChatCompletion(completionRequest)

        if (completion.choices.isEmpty()) {
            return null
        }

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
        completion.getChoices().forEach({choice -> logger.info(choice.message.content) })
        return NlpResponse("Chat", mapOf("choice" to completion.choices.first().message.content))
    }

    private fun showNotificationForApiKey() {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Idiolect")
            .createNotification("OpenAI not configured",
                """
                    <p>Create and configure OpenAI API key<p>
                    <p><a href="https://platform.openai.com/account/api-keys">https://platform.openai.com/account/api-keys</a></p>
                """.trimIndent(), NotificationType.INFORMATION
            )
            .notify(null)
    }
}
