package org.openasr.idear.actions.recognition

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import org.openasr.idear.actions.ActionRoutines
import java.awt.Component

class JavaActionRecognizer : MultiSentenceActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?): Boolean {
        return (component is EditorComponentImpl
                && dataContext.getData(PlatformCoreDataKeys.FILE_EDITOR)?.file?.fileType is JavaFileType)
                // or allow "new class" when a package is selected
                || dataContext.getData(PlatformCoreDataKeys.SELECTED_ITEMS)?.get(0) is PsiJavaDirectoryImpl
    }

    override fun getHandler(utterance: String): SpeechActionHandler? {
        when {
            "public static void main" in utterance -> ActionRoutines.routinePsvm()
            "print line" in utterance -> ActionRoutines.routinePrintln()
            // "create new class (optional name)"
            "new class" in utterance -> ActionRoutines.routineAddNewClass(utterance)
            else -> return null
        }

        return { _: String, _: DataContext -> ActionCallInfo.RoutineActioned }
    }
}
