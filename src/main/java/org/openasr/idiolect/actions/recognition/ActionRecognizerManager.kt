package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.CONTEXT_COMPONENT
import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idiolect.nlp.*


open class ActionRecognizerManager(private val dataContext: DataContext) {
    private var EP_NAME = ExtensionPointName<ActionRecognizer>("org.openasr.idiolect.actionRecognizer")

    fun documentGrammars(formatter: (recognizer: ActionRecognizer, List<NlpGrammar>) -> List<String>): List<String> {
        val extensions = getExtensions()
        val ideaActionRecognizer = extensions.find { it.javaClass == RegisteredActionRecognizer::class.java }!!
        val editorActionRecognizer = extensions.find { it.javaClass == RegisteredEditorActionRecognizer::class.java }!!
        var ideaGrammars = ideaActionRecognizer.grammars
        val editorGrammars =
            ideaGrammars.filter { it.intentName.startsWith("Editor") } + editorActionRecognizer.grammars

        ideaGrammars = ideaGrammars.filter { !it.intentName.startsWith("Editor") }

        return extensions
                .sortedBy { it.order }
                .flatMap { recognizer ->
                    val grammars = when (recognizer) {
                        is RegisteredEditorActionRecognizer -> editorGrammars
                        is RegisteredActionRecognizer -> ideaGrammars
                        else -> recognizer.grammars
                    }
                    formatter.invoke(recognizer, grammars)
                }
    }

    /*
     * TODO: Split ActionRecognizer in two
     *
     * NlpHandler
     *   - DelegatingNlpHandler      .tryResultIntent -> NlpResponse?
     *     - UserCustomNlpHandler                       { intentName: Idiolect.FindUsages, slots {type: method}
     *     - IdiolectOfflineNlpHandler
     *     - DialogFlowNlpHandler
     *
     * IntentHandler
     *   - IdiolectIntentHandler if (intentName.startsWith("Idiolect.
     *      - FindUsagesHandler ([Idiolect.FindUsages])
     *   - TemplateHandler    if (intentName.startsWith("Template.") return ActionCallInfo(
     *   - RegisteredActionHandler  // tries to match intentName as ActionId
     */


    fun handleNlpRequest(nlpRequest: NlpRequest): ActionCallInfo? =
        getExtensions().filter { it.isSupported(dataContext, dataContext.getData(CONTEXT_COMPONENT)) }
            .firstNotNullOfOrNull { it.tryResolveIntent(nlpRequest, dataContext) }

    protected open fun getExtensions() = EP_NAME.extensions
}
