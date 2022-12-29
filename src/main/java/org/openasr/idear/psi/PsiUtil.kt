package org.openasr.idear.psi

import com.intellij.openapi.editor.Editor
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

object PsiUtil {
    fun Editor.findElementUnderCaret(): PsiElement? =
        project.let {
            if (it == null) return null
            else PsiDocumentManager.getInstance(it).getPsiFile(document)?.findElementAt(caretModel.offset)
        }

    fun PsiElement.findContainingClass() = PsiTreeUtil.getParentOfType(this, PsiClass::class.java)
}
