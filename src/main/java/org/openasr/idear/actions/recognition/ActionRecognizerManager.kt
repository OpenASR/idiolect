package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.*
import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRequest


open class ActionRecognizerManager(private val dataContext: DataContext) {
    private var EP_NAME = ExtensionPointName<ActionRecognizer>("org.openasr.idear.actionRecognizer")

    fun documentGrammars(formatter: (recognizer: ActionRecognizer, List<NlpGrammar>) -> List<String>): List<String> {
        val extensions = getExtensions()
        val ideaActionRecognizer = extensions.find { it.javaClass == RegisteredActionRecognizer::class.java }!!
        val editorActionRecognizer = extensions.find { it.javaClass == RegisteredEditorActionRecognizer::class.java }!!
        var ideaGrammars = ideaActionRecognizer.grammars
        val editorGrammars = ideaGrammars
                .filter { it.intentName.startsWith("Editor") }
                .plus( editorActionRecognizer.grammars )

        ideaGrammars = ideaGrammars
                .filter { !it.intentName.startsWith("Editor") }

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

    fun handleNlpRequest(nlpRequest: NlpRequest): ActionCallInfo? =
        getExtensions().filter { it.isSupported(dataContext, dataContext.getData(CONTEXT_COMPONENT)) }
            .firstNotNullOfOrNull { it.tryResolveIntent(nlpRequest, dataContext) }

    protected open fun getExtensions() = EP_NAME.extensions
}
