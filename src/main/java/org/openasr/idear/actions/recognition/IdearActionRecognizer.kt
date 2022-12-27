package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.asr.GrammarService
import org.openasr.idear.nlp.Commands
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.tts.TTSService
import java.awt.Component

class IdearActionRecognizer : ActionRecognizer("Idear Commands", 0) {
    override val grammars = listOf(
            object : NlpGrammar("Idear.Hi") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    TTSService.say("Hi!")
                    return ActionCallInfo(intentName, true)
                }
            }.withExample(Commands.HI_IDEA),


            object : NlpGrammar("Idear.About") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    ActionRoutines.routineAbout()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("tell me about yourself"),

            object : NlpGrammar("Idear.Pause") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    ActionRoutines.pauseSpeech()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("stop listening"),

//            object : NlpGrammar("Idear.Command") {
//                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
//                    GrammarService.useCommandGrammar()
//                    return ActionCallInfo(intentName, true)
//                }
//            }.withExample("command mode"),
//
//            object : NlpGrammar("Idear.Dictation") {
//                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
//                    GrammarService.useDictationGrammar()
//                    return ActionCallInfo(intentName, true)
//                }
//            }.withExample("dictation mode"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = true
}
