package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import org.openasr.idear.psi.PsiUtil.findElementUnderCaret
import org.openasr.idear.tts.TTSService

class WhereAmIAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext

        val editor = CommonDataKeys.EDITOR.getData(dataContext)

        var element = editor!!.findElementUnderCaret()

        val path = StringBuilder()

        while (element != null && element.parent != null) {
            element = element.parent
            if (element is PsiMethod) {
                path.append(" inside method ").append(element.name)
            } else if (element is PsiClass) {
                path.append(" in class ").append(element.name)
            }
        }

        TTSService.say("You are" + path.toString())
    }
}
