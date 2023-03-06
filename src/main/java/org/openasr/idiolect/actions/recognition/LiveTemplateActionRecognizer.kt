package org.openasr.idiolect.actions.recognition

import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.ide.PsiFileNoWhiteSpace
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

// Windows: %HOMEPATH%\AppData\Roaming\JetBrains\IntelliJIdea<version>\templates\
// Linux:   ~IntelliJ IDEA<version>\config\templates
// OS X:    ~/Library/Preferences/IntelliJ IDEA<version>/templates
class LiveTemplateActionRecognizer : IntentResolver("Live Templates", 900) {
    // built-in actions: "InsertLiveTemplate", "NewFromTemplate",
    // "Next/PrevTemplateParameter", "Next/PreviousTemplateVariable"
    // "SaveAsTemplate", "SaveFileAsTemplate"
    // "SurroundWithLiveTemplate"
    // select virtual template element
    // expand live template by tab

    override fun isSupported(dataContext: DataContext, component: Component?): Boolean =
        (component is EditorComponentImpl)

    /**
     * For documentation only, intent is resolved against applicable templates at run-time.
     * Some example phrases here are not good for recognition, but could be fixed by CustomUtteranceActionRecognizer
     */
    override val grammars = TemplateSettings.getInstance().templates.flatMap { template ->
        val key = formatKey(template.key)
        val example = getExamplePhraseForTemplate(template, key)
        val group = template.groupName.replace(" ", "")
        val keyGrammar = NlpGrammar("Template.${group}.${key}").withExample(example)

        if (template.id != null) {
            listOf(NlpGrammar("Template.id.${template.id}").withExample(example), keyGrammar)
        } else {
            listOf(keyGrammar)
        }
    }

    override fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext): NlpResponse? {
        val editor = dataContext.getData(PlatformCoreDataKeys.EDITOR)!!
        val file = dataContext.getData(PlatformCoreDataKeys.PSI_FILE)!!
        val templateCallback = CustomTemplateCallback(editor, file)

        val isSurrounding = false
        val templateContext = TemplateActionContext.create(
            PsiFileNoWhiteSpace(file),
            editor,
            templateCallback.offset, templateCallback.offset,
            isSurrounding
        )

        val applicableTemplates = TemplateManagerImpl.listApplicableTemplates(templateContext)
            .filter {
                nlpRequest.alternatives.contains(getExamplePhraseForTemplate(it, formatKey(it.key)))
            }

        return if (applicableTemplates.isEmpty()) null
        else {
            val intentName = applicableTemplates.first().run {
                if (id != null) "Template.id.${id}" else "Template.${groupName}.${key}"
            }

            return NlpResponse(intentName)
        }
    }

    private fun formatKey(key: String) = key.replace(Regex("[^a-zA-Z0-9]"), "_")

    private fun getExamplePhraseForTemplate(template: TemplateImpl, key: String): String {
        var example = template.description!!
            .replace("!", " bang")
            .replace(Regex("</[^>]+>"), "")
            .replace(Regex("[^\\w ]"), " ")
            .replace(Regex(" {2,}"), " ")
            .trim()
            .lowercase()

        if (example.isEmpty()) {
            example = key.replace("_", " ").trim()
        }

        return "template $example"
    }
}
