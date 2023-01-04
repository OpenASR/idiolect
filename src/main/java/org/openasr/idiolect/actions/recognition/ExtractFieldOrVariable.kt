package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

/** https://www.jetbrains.com/help/idea/extract-field.html */
class ExtractFieldOrVariable : IntentResolver("Extract Field or Variable", 500) {
    companion object {
        val INTENT_NAME = "${INTENT_PREFIX_IDIOLECT_ACTION}Extract"
    }

    override val grammars = listOf(
        object : NlpRegexGrammar(INTENT_NAME, "extract.* (variable|field|parameter) ?(.*)") {
            private val actionIds = mapOf(
                "variable" to "IntroduceVariable",
                "field" to "IntroduceField",
                "parameter" to "IntroduceParameter",
            )

            override fun createNlpResponse(utterance: String, values: List<String>, dataContext: DataContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                return NlpResponse(intentName, mapOf(
                    "actionId" to actionIds[values[1]]!!,
                    "name" to values[2]
                ))
            }
        }.withExamples(
            // NlpRegexGrammar does not use the examples, but they could be used in documentation
            "extract variable 'sum'",
            "extract to field",
            "extract this as a parameter"
        )
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
}
