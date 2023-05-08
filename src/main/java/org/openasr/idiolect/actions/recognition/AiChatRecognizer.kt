package org.openasr.idiolect.actions.recognition

import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.intent.handlers.IdiolectChatIntentHandler
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver

class AiChatRecognizer : IntentResolver("AI Powered", 600) {
    companion object {
        val INTENT_EXPLAIN_CLASS = "${IdiolectChatIntentHandler.INTENT_PREFIX}ExplainClass"
        val INTENT_EXPLAIN_FUNCTION = "${IdiolectChatIntentHandler.INTENT_PREFIX}ExplainFunction"
        val INTENT_TESTS_FOR_CLASS = "${IdiolectChatIntentHandler.INTENT_PREFIX}TestsForClass"
        val INTENT_TESTS_FOR_FUNCTION = "${IdiolectChatIntentHandler.INTENT_PREFIX}TestsForFunction"
    }

    override val grammars = listOf(
        NlpGrammar(INTENT_EXPLAIN_CLASS).withExamples("explain this class", "what does this class do"),
        NlpGrammar(INTENT_EXPLAIN_FUNCTION).withExamples("explain this function", "what does this function do"),

        NlpGrammar(INTENT_TESTS_FOR_CLASS).withExample("write tests for this class"),
        NlpGrammar(INTENT_TESTS_FOR_FUNCTION).withExample("write tests for this function")
    )
}
