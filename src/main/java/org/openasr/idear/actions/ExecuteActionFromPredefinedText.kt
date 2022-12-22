package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.openasr.idear.actions.recognition.ActionRecognizerManager
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.NlpRequest

/**
 * A debugging aid to use one of the ActionRecognizer extension classes configured in `plugin.xml` to generate
 * an ActionCallInfo which is then #runInEditor() ExecuteActionByCommandText
 */
object ExecuteActionFromPredefinedText : ExecuteActionByCommandText() {
    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = IdeService.getEditor(dataContext)!!

        // String text = "idea extract to field";
        val text = "idea rename to my super test"
        // String text = "idea inline";

        ActionRecognizerManager(e.dataContext).handleNlpRequest(NlpRequest(listOf(text)))?.let {
            runInEditor(editor, it)
        }
    }
}
