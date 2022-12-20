package org.openasr.idear.actions.recognition

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRegexGrammar
import java.awt.Component

class JavaActionRecognizer : ActionRecognizer("Java Shortcuts", 1000) {
    override val grammars = listOf(
            object : NlpGrammar("Java.Main") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    ActionRoutines.routinePsvm()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("public static void main"),

            object : NlpGrammar("Java.PrintLine") {
                override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo {
                    ActionRoutines.routinePrintln()
                    return ActionCallInfo(intentName, true)
                }
            }.withExample("print line"),

            // "create new class (optional name)"
            object : NlpRegexGrammar("Java.NewClass", ".*new class ?(.*)?") {
                override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo {
                    ActionRoutines.routinePsvm()
                    return ActionCallInfo(intentName, true).apply {
                        ActionRoutines.routineAddNewClass(values[1])
                    }
                }
            }.withExamples("new class", "create new class 'my demo'"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?): Boolean {
        return (component is EditorComponentImpl
                && dataContext.getData(PlatformCoreDataKeys.FILE_EDITOR)?.file?.fileType is JavaFileType)
                // or allow "new class" when a package is selected
                || dataContext.getData(PlatformCoreDataKeys.SELECTED_ITEMS)?.get(0) is PsiJavaDirectoryImpl
    }
}
