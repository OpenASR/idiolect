package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.asr.GrammarService
import org.openasr.idear.nlp.NlpGrammar
import java.awt.Component

class IdearActionRecognizer : ActionRecognizer {
    private val grammars = listOf(
            object : NlpGrammar("Idear.Pause") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    ActionRoutines.pauseSpeech()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("stop listening"),

            object : NlpGrammar("Idear.Command") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    GrammarService.useCommandGrammar()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("command mode"),

            object : NlpGrammar("Idear.Dictation") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    GrammarService.useDictationGrammar()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("dictation mode"),
    )

    override fun getGrammars(): List<NlpGrammar> = grammars

    override fun isSupported(dataContext: DataContext, component: Component?) = true
}
