package org.openasr.idiolect.actions.recognition

import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass
import com.intellij.ide.DataManager
import com.intellij.ide.actions.ApplyIntentionAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpGrammar
import java.awt.Component
import java.util.*

class SurroundWithNoNullCheckRecognizer : ActionRecognizer("Surround with Not-Null Check", 600) {
    override val grammars = listOf(
        object : NlpGrammar("Idiolect.SurroundWithNullCheck") {
            override fun createActionCallInfo(dataContext: DataContext): ActionCallInfo =
                ActionCallInfo(intentName, true).also { surroundWithNullCheck(dataContext) }
        }.withExample("surround with not null check"),
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl

    private fun surroundWithNullCheck(dataContext: DataContext) {
        val editor = IdeService.getEditor(dataContext)

//        val project = IdeService.getProject(dataContext)
        if (editor != null) {
            val file = getFile(dataContext, editor)

            if (file != null) {
                val info = ShowIntentionsPass.IntentionsInfo()
                ApplicationManager.getApplication().runReadAction { ShowIntentionsPass.getActionsToShow(editor, file, info, -1) }
                if (info.isEmpty) return

                val actions = ArrayList(info.run { errorFixesToShow + inspectionFixesToShow + intentionsToShow })
                val result = arrayOfNulls<ApplyIntentionAction>(actions.size)
                for (i in result.indices) {
                    val descriptor = actions[i]
                    val actionText = ApplicationManager.getApplication().runReadAction(Computable { descriptor.action.text })
                    result[i] = ApplyIntentionAction(descriptor, actionText, editor, file)
                }

                DataManager.getInstance().run {
                    val context = getDataContext(editor.contentComponent)
                    result[1]!!.actionPerformed(AnActionEvent(null, context, "", Presentation("surround with not null"),
                            ActionManager.getInstance(), 0))
                }
            }
        }
    }

    private fun getFile(dataContext: DataContext, editor: Editor): PsiFile? =
        IdeService.getProject(dataContext)?.let { project ->
            PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        }
}
