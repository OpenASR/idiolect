package org.openasr.idiolect.nlp

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import com.intellij.openapi.diagnostic.logger

open class NlpGrammar(val intentName: String, val rank: Int = Int.MAX_VALUE) {
    companion object {
        private val log = logger<NlpGrammar>()
    }

    lateinit var examples: Array<out String>

    fun withExample(example: String) = also { examples = arrayOf(example) }

    fun withExamples(vararg examples: String) = also { this.examples = examples }

    fun withExamples(examples: List<String>) = also { this.examples = examples.toTypedArray() }

    /**
     * Delegates to `tryMatchRequest(string)` for an exact match between the examples
     * @return a simple [ActionCallInfo] with the [intentName] as [ActionCallInfo.actionId]
     */
    open fun tryMatchRequest(nlpRequest: NlpRequest, dataContext: DataContext): NlpResponse? =
        nlpRequest.alternatives.firstNotNullOfOrNull { tryMatchRequest(it, dataContext) }

    /**
     * Checks for an exact match between the examples
     * @return a simple [ActionCallInfo] with the [intentName] as [ActionCallInfo.actionId]
     */
    open fun tryMatchRequest(utterance: String, dataContext: DataContext): NlpResponse? =
        if (examples.contains(utterance)) { createNlpResponse(utterance, dataContext) } else null

    open fun createNlpResponse(utterance: String, dataContext: DataContext) = createNlpResponse(utterance, intentName)

    open fun createNlpResponse(utterance: String, intentName: String): NlpResponse {
        logUtteranceForIntent(utterance, intentName)
        return NlpResponse(intentName)
    }

    protected fun logUtteranceForIntent(utterance: String, intentName: String) {
        log.info("Grammar matched intent for '${utterance}': $intentName")
    }
}

open class NlpRegexGrammar(intentName: String, pattern: String) : NlpGrammar(intentName) {
    private val regex = Regex(pattern)

    override fun tryMatchRequest(utterance: String, dataContext: DataContext): NlpResponse? =
        regex.matchEntire(utterance)?.let { match -> createNlpResponse(utterance, match.groupValues, dataContext) }

    open fun createNlpResponse(utterance: String, values: List<String>, dataContext: DataContext) =
        createNlpResponse(utterance, intentName)
}
