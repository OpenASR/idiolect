package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.intent.handlers.IdearCommandIntentHandler
import org.openasr.idear.nlp.intent.resolvers.IntentResolver
import org.openasr.idear.tts.TtsService
import java.awt.Component

class IdearCommandRecognizer : IntentResolver("Idear Commands", 0) {
    companion object {
        val INTENT_HI = "${IdearCommandIntentHandler.INTENT_PREFIX}Hi"
        val INTENT_ABOUT = "${IdearCommandIntentHandler.INTENT_PREFIX}About"
        val INTENT_PAUSE = "${IdearCommandIntentHandler.INTENT_PREFIX}Pause"
    }

    override val grammars = listOf(
        NlpGrammar(INTENT_HI).withExamples("hi idea", "hello"),
        NlpGrammar(INTENT_ABOUT).withExample("tell me about yourself"),
        NlpGrammar(INTENT_PAUSE).withExample("stop listening"),

//        NlpGrammar("Idear.Command").withExample("command mode")
//                ActionCallInfo(intentName, true).also { GrammarService.useCommandGrammar() }
//
//        NlpGrammar("Idear.Dictation").withExample("dictation mode")
//                ActionCallInfo(intentName, true).also { GrammarService.useDictationGrammar() }
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = true
}
