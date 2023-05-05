package org.openasr.idiolect.actions.recognition

import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.handlers.IdiolectCommandIntentHandler
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

class IdiolectCommandRecognizer : IntentResolver("idiolect Commands", 0) {
    companion object {
        val INTENT_HI = "${IdiolectCommandIntentHandler.INTENT_PREFIX}Hi"
        val INTENT_ABOUT = "${IdiolectCommandIntentHandler.INTENT_PREFIX}About"
        val INTENT_PAUSE = "${IdiolectCommandIntentHandler.INTENT_PREFIX}Pause"
        val INTENT_COMMANDS = "${IdiolectCommandIntentHandler.INTENT_PREFIX}Commands"
        val INTENT_EDIT_PHRASES = "${IdiolectCommandIntentHandler.INTENT_PREFIX}EditPhrases"
        // Keep the mode commands separate so that they can be betters supported by custom phrases
        val INTENT_ACTION_MODE = "${IdiolectCommandIntentHandler.INTENT_PREFIX}ActionMode"
        val INTENT_CHAT_MODE = "${IdiolectCommandIntentHandler.INTENT_PREFIX}ChatMode"
        val INTENT_EDIT_MODE = "${IdiolectCommandIntentHandler.INTENT_PREFIX}EditMode"
    }

    override val grammars = listOf(
        NlpGrammar(INTENT_HI).withExamples("hi idea", "hello"),
        NlpGrammar(INTENT_ABOUT).withExample("tell me about yourself"),
        NlpGrammar(INTENT_PAUSE).withExample("stop listening"),
        NlpGrammar(INTENT_EDIT_PHRASES).withExample("edit custom phrases"),

        object : NlpRegexGrammar(INTENT_COMMANDS, "what can i say(?: about (.*))?") {
            override fun createNlpResponse(utterance: String, values: List<String>, context: NlpContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(intentName, mapOf(IdiolectCommandIntentHandler.SLOT_COMMAND_TERM to values[1]))
            }
        }.withExamples("what can i say", "what can i say about 'template'"),

        NlpGrammar(INTENT_ACTION_MODE).withExamples("action mode", "command mode"),
        NlpGrammar(INTENT_CHAT_MODE).withExamples("bot mode", "chat mode", "ai mode"),
        NlpGrammar(INTENT_EDIT_MODE).withExamples("dictation mode", "edit mode", "typing mode")
    )

    override fun isSupported(context: NlpContext, component: Component?) = true
}
