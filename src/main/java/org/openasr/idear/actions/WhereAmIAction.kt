package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.*
import org.openasr.idear.ide.IDEService
import org.openasr.idear.psi.PsiUtil.findElementUnderCaret
import org.openasr.idear.tts.TTSService

object WhereAmIAction : IdearAction() {
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
