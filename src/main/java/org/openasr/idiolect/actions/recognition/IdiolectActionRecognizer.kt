package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.nlp.Commands
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.tts.TtsService
import java.awt.Component

class IdiolectActionRecognizer : ActionRecognizer("Idiolect Commands", 0) {
    override val grammars = listOf(
        object : NlpGrammar("Idiolect.Hi") {
            override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                ActionCallInfo(intentName, true).also { TtsService.say("Hi!") }
        }.withExample(Commands.HI_IDEA),

        object : NlpGrammar("Idiolect.About") {
            override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                ActionCallInfo(intentName, true).also { ActionRoutines.routineAbout() }
        }.withExample("tell me about yourself"),

        object : NlpGrammar("Idiolect.Pause") {
            override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                ActionCallInfo(intentName, true).also { ActionRoutines.pauseSpeech() }
        }.withExample("stop listening"),

//        object : NlpGrammar("Idiolect.Command") {
//            override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
//                ActionCallInfo(intentName, true).also { GrammarService.useCommandGrammar() }
//        }.withExample("command mode"),
//
//        object : NlpGrammar("Idiolect.Dictation") {
//            override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
//                ActionCallInfo(intentName, true).also { GrammarService.useDictationGrammar() }
//        }.withExample("dictation mode"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = true
}
