package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.asr.GrammarService
import org.openasr.idear.nlp.NlpGrammar
import java.awt.Component

class IdearActionRecognizer : ActionRecognizer("Idear Commands", 0) {
    override val grammars = listOf(
            object : NlpGrammar("Idear.Pause") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                    ActionCallInfo(intentName, true).also { ActionRoutines.pauseSpeech() }
            }.withExample("stop listening"),

            object : NlpGrammar("Idear.Command") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                    ActionCallInfo(intentName, true).also { GrammarService.useCommandGrammar() }
            }.withExample("command mode"),

            object : NlpGrammar("Idear.Dictation") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                    ActionCallInfo(intentName, true).also { GrammarService.useDictationGrammar() }
            }.withExample("dictation mode"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = true
}
