package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

/**
 * "rename"
 */
class RenameActionRecognizer : IntentResolver("Rename", 500) {
    companion object {
        val INTENT_NAME = "${INTENT_PREFIX_IDIOLECT_ACTION}Rename"
    }

    override val grammars = listOf(
        object : NlpRegexGrammar(INTENT_NAME, "rename(?: to|as)? ?(.*)?") {
            override fun createNlpResponse(utterance: String, values: List<String>, context: NlpContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(intentName, mapOf("name" to values[1]))
            }
        }.withExamples(
            "rename",
            "rename to 'example'",
            "rename as 'something better'"
        )
    )

    override fun isSupported(context: NlpContext, component: Component?) = component is EditorComponentImpl
}
