package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.openasr.idiolect.actions.recognition.ActionRecognizerManager
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpRequest

/**
 * A debugging aid to use one of the
 * [org.openasr.idiolect.actions.recognition.ActionRecognizer]
 * extension classes configured in plugin.xml to generate
 * an [org.openasr.idiolect.actions.recognition.ActionCallInfo] which is then
 * [org.openasr.idiolect.actions.ExecuteActionByCommandText.runInEditor]
 */
object ExecuteActionFromPredefinedText : ExecuteActionByCommandText() {
    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = IdeService.getEditor(dataContext)!!

        // String text = "idea extract to field";
        val text = "idea rename to my super test"
        // String text = "idea inline";

        ActionRecognizerManager(e.dataContext).handleNlpRequest(NlpRequest(listOf(text)))
            ?.let { runInEditor(editor, it) }
    }
}
