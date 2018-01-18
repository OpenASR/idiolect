package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import org.openasr.idear.actions.recognition.TextToActionConverter
import org.openasr.idear.ide.IDEService

object ExecuteActionFromPredefinedText : ExecuteActionByCommandText() {
    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = IDEService.getEditor(dataContext)!!

        // String text = "idea extract to field";
        val text = "idea rename to my super test"
        // String text = "idea inline";

        TextToActionConverter(e.dataContext).extractAction(text)?.let { runInEditor(editor, it) }
    }
}