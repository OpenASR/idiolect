package org.openasr.idear.nlp

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.recognition.ActionCallInfo

open class NlpGrammar(val intentName: String, val rank: Int = Int.MAX_VALUE) {
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

    open fun createNlpResponse(utterance: String, dataContext: DataContext) = NlpResponse(intentName)
}

open class NlpRegexGrammar(intentName: String, pattern: String) : NlpGrammar(intentName) {
    private val regex = Regex(pattern)

    override fun tryMatchRequest(utterance: String, dataContext: DataContext): NlpResponse? =
        regex.matchEntire(utterance)?.let { match -> createNlpResponse(match.groupValues, dataContext) }

    open fun createNlpResponse(values: List<String>, dataContext: DataContext) = NlpResponse(intentName)
}
