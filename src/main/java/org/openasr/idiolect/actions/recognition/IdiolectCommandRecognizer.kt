package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.intent.handlers.IdiolectCommandIntentHandler
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

class IdiolectCommandRecognizer : IntentResolver("idiolect Commands", 0) {
    companion object {
        val INTENT_HI = "${IdiolectCommandIntentHandler.INTENT_PREFIX}Hi"
        val INTENT_ABOUT = "${IdiolectCommandIntentHandler.INTENT_PREFIX}About"
        val INTENT_PAUSE = "${IdiolectCommandIntentHandler.INTENT_PREFIX}Pause"
    }

    override val grammars = listOf(
        NlpGrammar(INTENT_HI).withExamples("hi idea", "hello"),
        NlpGrammar(INTENT_ABOUT).withExample("tell me about yourself"),
        NlpGrammar(INTENT_PAUSE).withExample("stop listening"),

//        NlpGrammar("idiolect.Command").withExample("command mode")
//                ActionCallInfo(intentName, true).also { GrammarService.useCommandGrammar() }
//
//        NlpGrammar("idiolect.Dictation").withExample("dictation mode")
//                ActionCallInfo(intentName, true).also { GrammarService.useDictationGrammar() }
    )

    override fun isSupported(context: NlpContext, component: Component?) = true
}
