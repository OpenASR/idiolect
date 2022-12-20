package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.extensions.ExtensionPointName
import io.ktor.util.reflect.*
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRequest


open class ActionRecognizerManager(private val dataContext: DataContext) {
    private var EP_NAME = ExtensionPointName<ActionRecognizer>("org.openasr.idear.actionRecognizer")

    fun listGrammarExamples(): List<String> {
        return getExtensions().flatMap { recognizer ->
            recognizer.grammars.flatMap { it.examples.toList() }
        }
    }

    fun documentGrammars(): List<String> {
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

                    listOf( "\n## ${recognizer.displayName}")
                            .plus(presentExamples(grammars))
                }
    }

    fun handleNlpRequest(nlpRequest: NlpRequest): ActionCallInfo? =
        getExtensions()
                .filter { it.isSupported(dataContext, dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)) }
                .firstNotNullOfOrNull { it.tryResolveIntent(nlpRequest, dataContext) }

    open protected fun getExtensions() = EP_NAME.extensions

    private fun presentExamples(grammars: List<NlpGrammar>): List<String> {
        return grammars.sortedBy { it.rank }
                .flatMap { grammar ->
                    grammar.examples
                            .sorted()
                            .map { " - $it" }
                }
    }
}
