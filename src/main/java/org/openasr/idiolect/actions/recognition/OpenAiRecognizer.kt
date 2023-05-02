package org.openasr.idiolect.actions.recognition

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

/*
Planning

- "command/chat/completion mode"
  - show ToolBar for chat/completion with edit & submit, auto submit option
  - configure which grammars are active
-

 */

class OpenAiRecognizer : IntentResolver("OpenAI", Int.MAX_VALUE) {
    private val logger = Logger.getInstance(javaClass)

    override val grammars = listOf(NlpGrammar("OpenAI").withExample("chat gpt"))

    init {
//        service = OpenAiService(OpenAiConfig.apiKey)
    }

    override fun isSupported(context: NlpContext, component: Component?): Boolean {

        return true
    }


    override fun tryResolveIntent(nlpRequest: NlpRequest, context: NlpContext): NlpResponse? {
        return null


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
