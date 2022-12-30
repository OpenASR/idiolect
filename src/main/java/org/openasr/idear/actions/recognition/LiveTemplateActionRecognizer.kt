package org.openasr.idear.actions.recognition

import com.intellij.codeInsight.template.CustomTemplateCallback
import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRequest
import java.awt.Component

// Windows: %HOMEPATH%\AppData\Roaming\JetBrains\IntelliJIdea<version>\templates\
// Linux:   ~IntelliJ IDEA<version>\config\templates
// OS X:    ~/Library/Preferences/IntelliJ IDEA<version>/templates
class LiveTemplateActionRecognizer : ActionRecognizer("Live Templates") {
    // built-in actions: "InsertLiveTemplate", "NewFromTemplate",
    // "Next/PrevTemplateParameter", "Next/PreviousTemplateVariable"
    // "SaveAsTemplate", "SaveFileAsTemplate"
    // "SurroundWithLiveTemplate"
    // select virtual template element
    // expand live template by tab
/*
    override val grammars = listOf(
        object : NlpRegexGrammar("Template.", )

        */

    /**
     * For documentation only, intent is resolved against applicable templates at run-time.
     * Some of the example phrases here are not good for recognition, but could be fixed by CustomUtteranceActionRecognizer
     */
    override val grammars = TemplateSettings.getInstance().templates.flatMap { template ->
        val example = getExamplePhraseForTemplate(template)
        val keyGrammar = NlpGrammar("Template.${template.groupName}.${template.key}").withExample(example)

        if (template.id != null) {
            listOf(NlpGrammar("Template.id.${template.id}").withExample(example), keyGrammar)
        } else {
            listOf(keyGrammar)
        }
    }

//    fun foo(dataContext: DataContext): Unit {
//        dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)
////        dataContext.getData(PlatformCoreDataKeys.FILE_EDITOR)?.file?.fileType is JavaFileType
////        dataContext.getData(PlatformCoreDataKeys.SELECTED_ITEMS)?.get(0) is PsiJavaDirectoryImpl
//    }

    private fun getExamplePhraseForTemplate(template: TemplateImpl) =
        if (template.description.isNullOrEmpty()) template.key else template.description!!.lowercase()

    override fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext): ActionCallInfo? {
        val templateManager = TemplateManager.getInstance(dataContext.getData(PlatformCoreDataKeys.PROJECT_CONTEXT))
//        templateManager.startTemplate()
//        TemplatesManager.TEMPLATE_IMPLICITS.  ???


//        TemplateContextTypes.getAllContextTypes().filter { templateContextType ->
//            templateContextType.isInContext()
//        }

        val editor = dataContext.getData(PlatformCoreDataKeys.EDITOR)!!
        val file = dataContext.getData(PlatformCoreDataKeys.PSI_FILE)!!

        val allTemplates = TemplateSettings.getInstance().templates   // should get all default & custom templates

//        TemplateSettings.getInstance().getTemplateById("id")  // don't count on id - probably null for custom templates


        val templateCallback = CustomTemplateCallback(editor, file)
        var key = "sout"
        val template = templateCallback.findApplicableTemplate(key)
        val applicableTemplatesForKey = templateCallback.findApplicableTemplates(key)  // uses TemplateSettings.templates but filters by context & deactivated
//        templateCallback.

        val isSurrounding = false
        val templateContext = TemplateActionContext.create(file, editor, templateCallback.offset, templateCallback.offset, isSurrounding)

        // inferior to TemplateManagerImpl.listApplicableTemplates()
//        allTemplates.filter { !it.isDeactivated && TemplateManagerImpl.isApplicable(template, templateContext) }




        val contextTypes = TemplateManagerImpl.getApplicableContextTypes(templateContext)
        val applicableTemplates = TemplateManagerImpl.listApplicableTemplates(templateContext)

        for (template in applicableTemplates) {
            if (nlpRequest.alternatives.contains(getExamplePhraseForTemplate(template))) {
                break
            }
        }


        if (applicableTemplates.isNotEmpty()) {
            val template = applicableTemplates.first()

            template.id
//            val intent = "Template.id.${template.id}" // null for custom templates
            template.key
            val intent = "Template.${template.key}"
            template.description

            templateManager.startTemplate(editor, template)
        }
//        templateManager.startTemplate(editor, 'c')

//        val customLiveTemplates = ContainerUtil.findAll(CustomLiveTemplate.EP_NAME.extensions) { customLiveTemplate ->
////            shortcutChar == customLiveTemplate.shortcut &&
////                (!multiCaretMode || TemplateManagerImpl.supportsMultiCaretMode(customLiveTemplate)) &&
//                TemplateManagerImpl.isApplicable(customLiveTemplate, templateContext)
//        }.first()


//        TemplateManagerUtilBase.getTemplateState()



        return object : NlpGrammar("Anonymous") {
            override fun tryMatchRequest(utterance: String, dataContext: DataContext): ActionCallInfo? {


                return null
            }
        }.tryMatchRequest(nlpRequest, dataContext)
    }

    override fun isSupported(dataContext: DataContext, component: Component?): Boolean =
        (component is EditorComponentImpl)
}
