package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

class FindUsagesActionRecognizer : IntentResolver("Find Usages", 500) {
    companion object {
        val INTENT_NAME = "${INTENT_PREFIX_IDIOLECT_ACTION}FindUsages"
    }

    override val grammars = listOf(
        object : NlpRegexGrammar(INTENT_NAME, "find usages of (field|method) ?(.*)?") {
            override fun createNlpResponse(utterance: String, values: List<String>, context: NlpContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(INTENT_NAME, mapOf(
                    "subject" to values[1],
                    "target" to values[2],
                ))
            }
        }.withExamples(
            "find usages",
            "find usages of field 'my field'",
            "find usages of method 'my method'"
        )
    )

    override fun isSupported(context: NlpContext, component: Component?) = component is EditorComponentImpl
}
