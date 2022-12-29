package org.openasr.idear.nlp

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.recognition.ActionCallInfo

open class NlpGrammar(val intentName: String, val rank: Int = Int.MAX_VALUE) {
    lateinit var examples: Array<out String>

    fun withExample(example: String) = also { examples = arrayOf(example) }

    fun withExamples(vararg examples: String) = also { this.examples = examples }

    /**
     * Delegates to `tryMatchRequest(string)` for an exact match between the examples
     * @return a simple [ActionCallInfo] with the [intentName] as [ActionCallInfo.actionId]
     */
    open fun tryMatchRequest(nlpRequest: NlpRequest, dataContext: DataContext): ActionCallInfo? =
        nlpRequest.alternatives.firstNotNullOfOrNull { tryMatchRequest(it, dataContext) }

    /**
     * Checks for an exact match between the examples
     * @return a simple [ActionCallInfo] with the [intentName] as [ActionCallInfo.actionId]
     */
    open fun tryMatchRequest(utterance: String, dataContext: DataContext): ActionCallInfo? =
        if (examples.contains(utterance)) { createActionCallInfo(dataContext) } else null

    open fun createActionCallInfo(dataContext: DataContext) = ActionCallInfo(intentName)
}

open class NlpRegexGrammar(intentName: String, pattern: String) : NlpGrammar(intentName) {
    private val regex = Regex(pattern)

    override fun tryMatchRequest(utterance: String, dataContext: DataContext): ActionCallInfo? =
        regex.matchEntire(utterance)?.let { match -> createActionCallInfo(match.groupValues, dataContext) }

    open fun createActionCallInfo(values: List<String>, dataContext: DataContext) = ActionCallInfo(intentName)
}
