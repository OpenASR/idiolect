package com.jetbrains.idear.psi;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

public class PsiUtil {

    public static PsiElement findElementUnderCaret(Editor editor, Project project) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());

        if (psiFile == null)
            return null;

        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    public static PsiClass findContainingClass(PsiElement e) {
        return PsiTreeUtil.getParentOfType(e, PsiClass.class);
    }

}
