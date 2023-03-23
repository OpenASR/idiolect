package org.openasr.idiolect.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.ui.components.JBTextField
import org.openasr.idiolect.asr.AsrService
import org.openasr.idiolect.nlp.NlpRequest

class ExecuteActionFromTextField : ExecuteActionByCommandText(), DumbAware {
    var textField: JBTextField? = null

    init {
        this.templatePresentation.icon = AllIcons.Actions.Execute
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (textField != null) {
            val nlpRequest = NlpRequest(listOf(textField!!.text))
            AsrService.onNlpRequest(nlpRequest)
        }
    }
}
