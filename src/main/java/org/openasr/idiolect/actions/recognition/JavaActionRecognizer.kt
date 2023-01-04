package org.openasr.idiolect.actions.recognition

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.*
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import org.openasr.idiolect.nlp.*
import org.openasr.idiolect.asr.AsrService
import org.openasr.idiolect.nlp.intent.handlers.JavaActionIntentHandler
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component

class JavaActionRecognizer : IntentResolver("Java Shortcuts", 1000) {
    companion object {
        val INTENT_NEW_CLASS = "${JavaActionIntentHandler.INTENT_PREFIX}NewClass"
    }

    override val grammars = listOf(
        NlpGrammar("Template.Java.psvm").withExample("public static void main"),
//                ActionCallInfo(intentName, true).also { ActionRoutines.routinePsvm() }

        NlpGrammar("Template.Java.sout").withExample("print line"),
//                ActionCallInfo(intentName, true).also { ActionRoutines.routinePrintln() }

        // "create new class (optional name)"
        object : NlpRegexGrammar(INTENT_NEW_CLASS, ".*new class ?(.*)?") {
            override fun createNlpResponse(utterance: String, values: List<String>, dataContext: DataContext): NlpResponse {
                logUtteranceForIntent(utterance, intentName)
                val className: String = values[1].ifEmpty {
                    AsrService.promptForUtterance("what shall we call it?")
                }

                return NlpResponse(intentName, mapOf("className" to className))
            }
        }.withExamples("new class", "create new class 'my demo'"),

        // TODO: convert to live template
        // https://www.jetbrains.com/help/idea/template-variables.html#pdtv
        // https://stackoverflow.com/a/50236952/1225993
//            object : NlpRegexGrammar("Java.NewMethod",
//                ".*(private|protected|public|package) ?(.*)? (?:method|function) ?(.*)?") {
//                override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo {
//                    val visibility = values[1]
//                    var type = values[2]
//                    var name = values[3]
//
//                    if (visibility != "package") {
//                        IdeService.type("$visibility ")
//                    }
//
//                    if (type.isEmpty()) {
//                        type = ActionRoutines.promptForReturnType()
//                    }
//                    IdeService.type("$type ")
//
//                    if (name.isEmpty()) {
//                        name = ActionRoutines.promptForName()
//                    }
//                    IdeService.type("$name() {\n}")
//
//                    return ActionCallInfo(intentName, true)
//                }
//            }.withExamples("public method", "create private int function 'my demo'"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?): Boolean =
        (component is EditorComponentImpl
            && dataContext.getData(FILE_EDITOR)?.file?.fileType is JavaFileType)
            // or allow "new class" when a package is selected
            || dataContext.getData(SELECTED_ITEMS)?.get(0) is PsiJavaDirectoryImpl
}
