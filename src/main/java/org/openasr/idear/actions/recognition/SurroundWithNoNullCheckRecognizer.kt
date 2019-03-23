package org.openasr.idear.actions.recognition

import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass
import com.intellij.ide.DataManager
import com.intellij.ide.actions.ApplyIntentionAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import org.openasr.idear.ide.IDEService
import java.util.*

class SurroundWithNoNullCheckRecognizer : ActionRecognizer {
    override fun isMatching(sentence: String) = sentence.contains("check") && sentence.contains("not")

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo? {
        val editor = IDEService.getEditor(dataContext)
        val project = IDEService.getProject(dataContext)

        if (project == null || editor == null) return null

        val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return null

        val info = ShowIntentionsPass.IntentionsInfo()
        ApplicationManager.getApplication().runReadAction { ShowIntentionsPass.getActionsToShow(editor, file, info, -1) }
        if (info.isEmpty) return null

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

        return null
    }
}


