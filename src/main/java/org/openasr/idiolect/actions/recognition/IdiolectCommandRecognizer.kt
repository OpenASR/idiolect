package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.asr.AsrService
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
    }

    override val grammars = listOf(
        NlpGrammar(INTENT_HI).withExamples("hi idea", "hello"),
        NlpGrammar(INTENT_ABOUT).withExample("tell me about yourself"),
        NlpGrammar(INTENT_PAUSE).withExample("stop listening"),
        object : NlpRegexGrammar(INTENT_COMMANDS, "what can i say(?: about (.*))?") {
            override fun createNlpResponse(utterance: String, values: List<String>, context: NlpContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(intentName, mapOf(IdiolectCommandIntentHandler.SLOT_COMMAND_TERM to values[1]))
            }
        }.withExamples("what can i say", "what can i say about 'template'"),

//        NlpGrammar("idiolect.Command").withExample("command mode")
//                ActionCallInfo(intentName, true).also { GrammarService.useCommandGrammar() }
//
//        NlpGrammar("idiolect.Dictation").withExample("dictation mode")
//                ActionCallInfo(intentName, true).also { GrammarService.useDictationGrammar() }
    )

    override fun isSupported(context: NlpContext, component: Component?) = true
}
