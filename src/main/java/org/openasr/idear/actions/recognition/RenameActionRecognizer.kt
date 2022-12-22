package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.nlp.NlpRegexGrammar
import org.openasr.idear.utils.toCamelCase
import java.awt.Component

/**
 * "rename"
 */
class RenameActionRecognizer : ActionRecognizer("Rename", 500) {
    override val grammars = listOf(
            object : NlpRegexGrammar(IdeActions.ACTION_RENAME, "rename(?: to|as)? ?(.*)?") {
                override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo {
                    return ActionCallInfo(intentName).apply {
                        val name = values[1]
                        if (name.isNotEmpty()) {
                            typeAfter = name.toCamelCase()
                            hitTabAfter = true
                        }
                    }
                }
            }.withExamples(
                    "rename",
                    "rename to 'example'",
                    "rename as 'something better'"
            )
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
}
