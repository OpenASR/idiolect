package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver

class FileNavigationRecognizer : IntentResolver("File Navigation", 800) {
    companion object {
        val INTENT_OF_LINE = "${INTENT_PREFIX_IDIOLECT_ACTION}OfLine"
        val INTENT_GO_TO_LINE = "${INTENT_PREFIX_IDIOLECT_ACTION}GoToLine"
        val INTENT_FOCUS = "${INTENT_PREFIX_IDIOLECT_ACTION}Focus"
    }
    override val grammars: List<NlpGrammar>
        get() = listOf(
            FileNavigationGrammar(INTENT_OF_LINE, "(beginning|end) of line", "position")
                .withExamples("beginning of line", "end of line"),

            FileNavigationGrammar(INTENT_GO_TO_LINE, "go to line (.+)", "line")
                .withExample("go to line 10"),

            FileNavigationGrammar(INTENT_FOCUS, "focus (editor|project|symbols)", "target")
                .withExamples("focus editor", "focus project", "focus symbols")
        )

    class FileNavigationGrammar(intentName: String, pattern: String, private val slotName: String) : NlpRegexGrammar(intentName, pattern) {
        override fun createNlpResponse(utterance: String, values: List<String>, dataContext: DataContext): NlpResponse {
            logUtteranceForIntent(utterance, intentName)
            return NlpResponse(intentName, mapOf(slotName to values[1]))
        }
    }
}
