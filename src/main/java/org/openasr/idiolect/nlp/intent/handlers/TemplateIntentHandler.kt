package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpResponse

class TemplateIntentHandler : IntentHandler {
    val intentRegex = Regex("Template\\.([^\\.]+)\\.(.+)")

    override fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo? {
        if (!response.intentName.startsWith("Template.")) {
            return null
        }

        val project = IdeService.getProject(dataContext)
        val templateManager = TemplateManager.getInstance(project)
        val editor = dataContext.getData(PlatformCoreDataKeys.EDITOR)!!

        val template = intentRegex.matchEntire(response.intentName)?.let { match ->
            val group = match.groupValues[1]
            val key = match.groupValues[2]

            if (group == "id") {
                TemplateSettings.getInstance().getTemplateById("id")
            } else {
                val file = dataContext.getData(PlatformCoreDataKeys.PSI_FILE)!!
                val templateCallback = CustomTemplateCallback(editor, file)

                val element = file.findElementAt(templateCallback.offset)
                if (element is PsiWhiteSpace) {
                    // JavaCodeContextType won't let us insert code into white space
                    TemplateSettings.getInstance().getTemplate(key, group)
                } else {
                    templateCallback.findApplicableTemplate(key)
                }
            }
        }

        if (template != null) {
            templateManager.startTemplate(editor, template)
            return ActionCallInfo(response.intentName, true)
        }

        return null
    }
}
