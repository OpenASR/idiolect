package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.*
import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.nlp.intent.handlers.IntentHandler
import org.openasr.idear.nlp.intent.resolvers.IntentResolver


open class ActionRecognizerManager(private val dataContext: DataContext) {
    private var RESOLVER_EP_NAME = ExtensionPointName<IntentResolver>("org.openasr.idear.intentResolver")
    private var HANDLER_EP_NAME = ExtensionPointName<IntentHandler>("org.openasr.idear.intentHandler")

    fun documentGrammars(formatter: (recognizer: IntentResolver, List<NlpGrammar>) -> List<String>): List<String> {
        val extensions = getResolvers()
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

    fun handleNlpRequest(nlpRequest: NlpRequest): ActionCallInfo? {
        val nlpResponse = getResolvers().filter { it.isSupported(dataContext, dataContext.getData(CONTEXT_COMPONENT)) }
            .firstNotNullOfOrNull { it.tryResolveIntent(nlpRequest, dataContext) }

        return if (nlpResponse == null) null else {
            getHandlers().firstNotNullOfOrNull { it.tryFulfillIntent(nlpResponse, dataContext) }
        }
    }


    protected open fun getResolvers() = RESOLVER_EP_NAME.extensions
    protected open fun getHandlers() = HANDLER_EP_NAME.extensions
}
