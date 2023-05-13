package org.openasr.idiolect.actions.recognition

import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.handlers.IdiolectCommandIntentHandler
import org.openasr.idiolect.nlp.intent.handlers.IdiolectNavigationIntentHandler
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

class IdiolectNavigationRecognizer : IntentResolver("idiolect Navigation", 0) {
    companion object {
        val INTENT_SWITCH_TO_TAB = "${IdiolectNavigationIntentHandler.INTENT_PREFIX}Tab"
        val INTENT_OPEN_PROJECT_FILE = "${IdiolectNavigationIntentHandler.INTENT_PREFIX}OpenProjectFile"
    }

    override val grammars = listOf(
        object : NlpRegexGrammar(INTENT_SWITCH_TO_TAB, "switch to (.*)") {
            override fun createNlpResponse(utterance: String, values: List<String>, context: NlpContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(intentName, mapOf(IdiolectNavigationIntentHandler.SLOT_NAME to values[1]))
            }
        }.withExamples("switch to 'MyClass'"),

        object : NlpRegexGrammar(INTENT_OPEN_PROJECT_FILE, "open (.*)") {
            override fun createNlpResponse(utterance: String, values: List<String>, context: NlpContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(intentName, mapOf(IdiolectNavigationIntentHandler.SLOT_NAME to values[1]))
            }
        }.withExamples("open 'MyClass'"),
    )

    override fun isSupported(context: NlpContext, component: Component?) = true
}
