package org.openasr.idear.psi

import com.intellij.openapi.editor.Editor
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

object PsiUtil {
    fun Editor.findElementUnderCaret(): PsiElement? {
        val p = project
        return if (p == null) null
        else PsiDocumentManager.getInstance(p).getPsiFile(document)?.findElementAt(caretModel.offset)
    }

    fun PsiElement.findContainingClass() = PsiTreeUtil.getParentOfType(this, PsiClass::class.java)
}
