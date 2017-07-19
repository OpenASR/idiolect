package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.actions.recognition.TextToActionConverter
import org.openasr.idear.ide.IDEService

object ExecuteVoiceCommandAction : ExecuteActionByCommandText() {
    private val logger = Logger.getInstance(ExecuteVoiceCommandAction::class.java)
    private val KEY = DataKey.create<String>("Idear.VoiceCommand.Text")

    override fun actionPerformed(e: AnActionEvent) {
        val editor = IDEService.getEditor(e.dataContext)!!

        val provider = TextToActionConverter(e.dataContext)
        val info = provider.extractAction(e.getData(KEY)!!)
        if (info != null) {
            runInEditor(editor, info)
        } else {
            logger.info("Command not recognized")
        }
    }
}
