package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.nlp.NlpRegexGrammar
import org.openasr.idear.nlp.NlpResponse
import org.openasr.idear.nlp.intent.resolvers.IntentResolver
import java.awt.Component

/**
 * "rename"
 */
class RenameActionRecognizer : IntentResolver("Rename", 500) {
    companion object {
        val INTENT_NAME = "${INTENT_PREFIX_IDEAR_ACTION}Rename"
    }

    override val grammars = listOf(
        object : NlpRegexGrammar(INTENT_NAME, "rename(?: to|as)? ?(.*)?") {
            override fun createNlpResponse(values: List<String>, dataContext: DataContext) =
                NlpResponse(intentName, mapOf("name" to values[1]))
        }.withExamples(
            "rename",
            "rename to 'example'",
            "rename as 'something better'"
        )
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
}
