package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import org.openasr.idear.ide.IDEService
import org.openasr.idear.psi.PsiUtil.findElementUnderCaret
import org.openasr.idear.tts.TTSService

class WhereAmIAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = IDEService.getEditor(e.dataContext)

        editor?.findElementUnderCaret()?.let {
            TTSService.say("You are in " + it.firstNamedParent())
        }
    }

    private tailrec fun PsiElement.firstNamedParent(): String? =
            when (this) {
                is PsiMethod -> "method " + name
                is PsiClass -> "class " + name
                is PsiFile -> "file " + name
                else -> parent.firstNamedParent()
            }
}
