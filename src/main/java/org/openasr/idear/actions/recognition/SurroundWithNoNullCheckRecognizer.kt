package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.intent.resolvers.IntentResolver
import java.awt.Component

class SurroundWithNoNullCheckRecognizer : IntentResolver("Surround with Not-Null Check", 600) {
    companion object {
        val INTENT_NAME = "${INTENT_PREFIX_IDEAR_ACTION}SurroundWithNullCheck"
    }

    override val grammars = listOf(
        NlpGrammar(INTENT_NAME).withExample("surround with not null check"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
}
