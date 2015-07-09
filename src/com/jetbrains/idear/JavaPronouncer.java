package com.jetbrains.idear;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class JavaPronouncer implements IntentionAction {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Pronounce";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Pronounce";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        SelectionModel selectionModel = editor.getSelectionModel();
        return psiFile.getLanguage() == JavaLanguage.INSTANCE && selectionModel.hasSelection();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
