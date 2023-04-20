package org.openasr.idiolect.actions.recognition

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.*
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.runBlocking
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
    private var openAI: OpenAI? = null

    init {
//        service = OpenAiService(OpenAiConfig.apiKey)
    }

    override fun isSupported(context: NlpContext, component: Component?): Boolean {
        if (openAI == null) {
            OpenAiConfig.apiKey.let {
                if (it.isNullOrEmpty()) {
    //            showNotificationForApiKey()
                    return false
                } else {
                    // TODO - subscribe for changes
                    openAI = OpenAI(it)
                }
            }
        }
        return true
    }

    @OptIn(BetaOpenAI::class)
    override fun tryResolveIntent(nlpRequest: NlpRequest, context: NlpContext): NlpResponse? {
        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(ChatRole.System, "You are a helpful pair programming assistant"),
//                ChatMessage(ChatMessageRole.ASSISTANT.value(), context),
                ChatMessage(ChatRole.User, nlpRequest.utterance)
            )
        )

        val completion = runBlocking { openAI?.chatCompletion(completionRequest) }

        if (completion == null || completion.choices.isEmpty()) {
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
        completion.choices.forEach { choice -> logger.info(choice.message?.content) }
        return NlpResponse("Chat", mapOf("choice" to (completion.choices.first().message?.content ?: "")))
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
