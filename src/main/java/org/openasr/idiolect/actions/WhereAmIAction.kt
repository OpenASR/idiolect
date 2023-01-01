package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.*
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.psi.PsiUtil.findElementUnderCaret
import org.openasr.idiolect.tts.TtsService

object WhereAmIAction : IdiolectAction() {
    override fun actionPerformed(e: AnActionEvent) {
        IdeService.getEditor(e.dataContext)?.findElementUnderCaret()?.let {
            TtsService.say("You are in " + it.firstNamedParent())
        }
    }

    private tailrec fun PsiElement.firstNamedParent(): String? =
        when (this) {
            is PsiMethod -> "method $name"
            is PsiClass -> "class $name"
            is PsiFile -> "file $name"
            else -> parent.firstNamedParent()
        }
}
