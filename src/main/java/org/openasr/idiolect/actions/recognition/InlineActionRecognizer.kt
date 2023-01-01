package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idiolect.nlp.NlpGrammar
import java.awt.Component

//class InlineActionRecognizer : ActionRecognizer("Inline ") {
//    private val grammar = listOf(
//            NlpGrammar("Inline")
//                    .withExample("inline")
//    )
//
//    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
//
//    override fun getGrammars(): List<NlpGrammar> = grammar
//}
