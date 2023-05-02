package org.openasr.idiolect.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import org.openasr.idiolect.nlp.ai.AiResponseListener
import org.openasr.idiolect.nlp.ai.AiService
import javax.swing.text.JTextComponent

class AiAction : AnAction() {
    private val aiService = service<AiService>()
    private lateinit var textField: JTextComponent



    init {
        this.templatePresentation.icon = AllIcons.Actions.Execute
    }

    fun setTextField(textField: JTextComponent) {
        this.textField = textField
    }

    override fun actionPerformed(e: AnActionEvent) {
        val prompt = textField.text

        aiService.sendCompletion(prompt)
    }
}
