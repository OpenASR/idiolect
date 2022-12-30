package org.openasr.idear.nlp.intent.handlers

import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.nlp.NlpResponse

class TemplateIntentHandler : IntentHandler {
    val intentRegex = Regex("Template\\.([^\\.]+)\\.(.+)")

    override fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo? {
        if (!response.intentName.startsWith("Template.")) {
            return null
        }

        val templateManager = TemplateManager.getInstance(dataContext.getData(PlatformCoreDataKeys.PROJECT_CONTEXT))
        val editor = dataContext.getData(PlatformCoreDataKeys.EDITOR)!!

        val template = intentRegex.matchEntire(response.intentName)?.let { match ->
            val group = match.groupValues[1]
            val key = match.groupValues[2]

            if (group == "id") {
                TemplateSettings.getInstance().getTemplateById("id")
            } else {
                val file = dataContext.getData(PlatformCoreDataKeys.PSI_FILE)!!
                val templateCallback = CustomTemplateCallback(editor, file)
                templateCallback.findApplicableTemplate(key)
            }
        }

        if (template != null) {
            templateManager.startTemplate(editor, template)
            return ActionCallInfo(response.intentName, true)
        }

        return null
    }
}
