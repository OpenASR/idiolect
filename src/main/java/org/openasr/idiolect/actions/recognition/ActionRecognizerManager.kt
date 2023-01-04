package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.CONTEXT_COMPONENT
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idiolect.nlp.intent.handlers.IntentHandler
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.nlp.*

open class ActionRecognizerManager(private val dataContext: DataContext) {
    companion object {
        private val log = logger<ActionRecognizerManager>()
    }

    private var RESOLVER_EP_NAME = ExtensionPointName<IntentResolver>("org.openasr.idiolect.intentResolver")
    private var HANDLER_EP_NAME = ExtensionPointName<IntentHandler>("org.openasr.idiolect.intentHandler")

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
            log.info("Intent: ${nlpResponse.intentName}")
            getHandlers().firstNotNullOfOrNull { it.tryFulfillIntent(nlpResponse, dataContext) }
        }
    }

    protected open fun getResolvers() = RESOLVER_EP_NAME.extensions
    protected open fun getHandlers() = HANDLER_EP_NAME.extensions
}
