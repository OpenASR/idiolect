package org.openasr.idear.nlp

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.recognition.ActionCallInfo

open class NlpGrammar(val intentName: String) {
    lateinit var examples: Array<out String>

    fun withExample(example: String): NlpGrammar {
        examples = arrayOf(example)
        return this
    }

    fun withExamples(vararg examples: String): NlpGrammar {
        this.examples = examples
        return this
    }

    /**
     * Delegates to `tryMatchRequest(string)` for an exact match between the examples
     * @return a simple ActionCallInfo with the intentName as actionId
     */
    open fun tryMatchRequest(nlpRequest: NlpRequest, dataContext: DataContext): ActionCallInfo? {
        return nlpRequest.alternatives.firstNotNullOfOrNull {
            tryMatchRequest(it, dataContext)
        }
    }

    /**
     * Checks for an exact match between the examples
     * @return a simple ActionCallInfo with the intentName as actionId
     */
    open fun tryMatchRequest(utterance: String, dataContext: DataContext): ActionCallInfo? {
        if (examples.contains(utterance)) {
            return createActionCallInfo(dataContext)
        }
        return null
    }

    open fun createActionCallInfo(dataContext: DataContext) = ActionCallInfo(intentName)
}

open class NlpRegexGrammar(intentName: String, pattern: String) : NlpGrammar(intentName) {
    private val regex = Regex(pattern)
    
    override fun tryMatchRequest(utterance: String, dataContext: DataContext): ActionCallInfo? {
        return regex.matchEntire(utterance)?.let { match ->
            createActionCallInfo(match.groupValues, dataContext)
        }
    }
    open fun createActionCallInfo(values: List<String>, dataContext: DataContext) = ActionCallInfo(intentName)
}
