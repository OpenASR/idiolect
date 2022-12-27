package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.util.Pair
import com.intellij.util.containers.ContainerUtil
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRegexGrammar
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.nlp.NlpResponse
import org.openasr.idear.utils.toCamelCase
import java.awt.Component
import java.util.*

/** https://www.jetbrains.com/help/idea/extract-field.html */
class ExtractActionRecognizer : ActionRecognizer("Extract Field or Variable", 500) {
    override val grammars = listOf(
        object : NlpRegexGrammar("Idear.Extract", "extract(?: to)? (variable|field) ?(.*)") {
            private val actionIds = mapOf(
                    "variable" to "IntroduceVariable",
                    "field" to "IntroduceField")

            override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo {
                return ActionCallInfo(actionIds[values[1]]!!).apply {
                    val name = values[2]
                    if (name.isNotEmpty()) {
                        typeAfter = name.toCamelCase()
                        hitTabAfter = true
                    }
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
