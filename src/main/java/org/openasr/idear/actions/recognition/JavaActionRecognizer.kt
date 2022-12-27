package org.openasr.idear.actions.recognition

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys.*
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.nlp.*
import java.awt.Component

class JavaActionRecognizer : ActionRecognizer("Java Shortcuts", 1000) {
    override val grammars = listOf(
            object : NlpGrammar("Java.Main") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                    ActionCallInfo(intentName, true).also { ActionRoutines.routinePsvm() }
            }.withExample("public static void main"),

            object : NlpGrammar("Java.PrintLine") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                    ActionCallInfo(intentName, true).also { ActionRoutines.routinePrintln() }
            }.withExample("print line"),

            // "create new class (optional name)"
            object : NlpRegexGrammar("Java.NewClass", ".*new class ?(.*)?") {
                override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo =
                    ActionCallInfo(intentName, true).also { ActionRoutines.routineAddNewClass(values[1]) }
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

    override fun isSupported(dataContext: DataContext, component: Component?): Boolean {
        return (component is EditorComponentImpl
                && dataContext.getData(FILE_EDITOR)?.file?.fileType is JavaFileType)
                // or allow "new class" when a package is selected
                || dataContext.getData(SELECTED_ITEMS)?.get(0) is PsiJavaDirectoryImpl
    }
}
