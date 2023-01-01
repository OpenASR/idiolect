package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.util.Pair
import com.intellij.util.containers.ContainerUtil
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.utils.toCamelCase
import java.awt.Component
import java.util.*

/** https://www.jetbrains.com/help/idea/extract-field.html */
class ExtractActionRecognizer : ActionRecognizer("Extract Field or Variable", 500) {
    override val grammars = listOf(
        object : NlpRegexGrammar("Idiolect.Extract", "extract(?: to)? (variable|field) ?(.*)") {
            private val actionIds = mapOf(
                "variable" to "IntroduceVariable",
                "field" to "IntroduceField"
            )

            override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo =
                ActionCallInfo(actionIds[values[1]]!!).apply {
                    val name = values[2]
                    if (name.isNotEmpty()) {
                        typeAfter = name.toCamelCase()
                        hitTabAfter = true
                    }
                }
        }.withExamples(
            // NlpRegexGrammar does not use the examples, but they could be used in documentation
            "extract variable 'sum'",
            "extract to field"
        )
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
}
