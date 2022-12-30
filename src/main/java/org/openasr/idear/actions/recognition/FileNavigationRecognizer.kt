package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRegexGrammar
import org.openasr.idear.nlp.NlpResponse
import org.openasr.idear.nlp.intent.resolvers.IntentResolver

class FileNavigationRecognizer : IntentResolver("File Navigation", 800) {
    companion object {
        val INTENT_OF_LINE = "${INTENT_PREFIX_IDEAR_ACTION}OfLine"
        val INTENT_GO_TO_LINE = "${INTENT_PREFIX_IDEAR_ACTION}GoToLine"
        val INTENT_FOCUS = "${INTENT_PREFIX_IDEAR_ACTION}Focus"
    }
    override val grammars: List<NlpGrammar>
        get() = listOf(
            object : NlpRegexGrammar(INTENT_OF_LINE, "(beginning|end) of line") {
                override fun createNlpResponse(values: List<String>, dataContext: DataContext): NlpResponse {
                    return NlpResponse(intentName, mapOf("position" to values[1]))
                }
            }.withExamples("beginning of line", "end of line"),

            object : NlpRegexGrammar(INTENT_GO_TO_LINE, "go to line (.+)") {
                override fun createNlpResponse(values: List<String>, dataContext: DataContext): NlpResponse {
                    return NlpResponse(intentName, mapOf("line" to values[1]))
                }
            }.withExample("go to line 10"),

            object : NlpRegexGrammar(INTENT_FOCUS, "focus (editor|project|symbols") {
                override fun createNlpResponse(values: List<String>, dataContext: DataContext): NlpResponse {
                    return NlpResponse(intentName, mapOf("target" to values[1]))
                }
            }.withExamples("focus editor", "focus project", "focus symbols")
        )
}
