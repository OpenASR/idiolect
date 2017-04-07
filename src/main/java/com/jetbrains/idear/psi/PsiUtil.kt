package com.jetbrains.idear.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

object PsiUtil {
    fun findElementUnderCaret(editor: Editor, project: Project): PsiElement? {
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return null
        return psiFile.findElementAt(editor.caretModel.offset)
    }

    fun findContainingClass(e: PsiElement): PsiClass? {
        return PsiTreeUtil.getParentOfType(e, PsiClass::class.java)
    }
}
