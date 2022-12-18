package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.asr.GrammarService
import java.awt.Component

class IdearActionRecognizer : MultiSentenceActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?) = true
    override fun getHandler(utterance: String): SpeechActionHandler? {
        when (utterance) {
            "stop listening" -> ActionRoutines.pauseSpeech()
            "command mode" -> GrammarService.useCommandGrammar()
            "dictation mode" -> GrammarService.useDictationGrammar()
            else -> return null
        }

        return { _: String, _: DataContext -> ActionCallInfo.RoutineActioned }
    }
}
