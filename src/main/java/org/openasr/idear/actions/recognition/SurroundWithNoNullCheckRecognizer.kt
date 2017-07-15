package org.openasr.idear.actions.recognition

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass
import com.intellij.ide.DataManager
import com.intellij.ide.actions.ApplyIntentionAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import java.util.*

class SurroundWithNoNullCheckRecognizer : ActionRecognizer {

    override fun isMatching(sentence: String): Boolean {
        return sentence.contains("check") && sentence.contains("not")
    }

    override fun getActionInfo(sentence: String,
                               dataContext: DataContext): ActionCallInfo? {
        val editor = CommonDataKeys.EDITOR.getData(dataContext)
        val project = CommonDataKeys.PROJECT.getData(dataContext)

        if (project == null || editor == null) return null

        val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return null

        val info = ShowIntentionsPass.IntentionsInfo()
        ApplicationManager.getApplication().runReadAction { ShowIntentionsPass.getActionsToShow(editor, file, info, -1) }
        if (info.isEmpty) return null

        val actions = ArrayList<HighlightInfo.IntentionActionDescriptor>()
        actions.addAll(info.errorFixesToShow)
        actions.addAll(info.inspectionFixesToShow)
        actions.addAll(info.intentionsToShow)

        val result = arrayOfNulls<ApplyIntentionAction>(actions.size)
        for (i in result.indices) {
            val descriptor = actions[i]
            val actionText = ApplicationManager.getApplication().runReadAction({ descriptor.action.text } as Computable<String>)
            result[i] = ApplyIntentionAction(descriptor, actionText, editor, file)
        }

        val nNull = result[1]!!

        val manager = DataManager.getInstance()
        if (manager != null) {
            val context = manager.getDataContext(editor.contentComponent)

            nNull.actionPerformed(AnActionEvent(null, context, "", Presentation("surround with not null"),
                    ActionManager.getInstance(), 0))
        }

        return null
    }
}


