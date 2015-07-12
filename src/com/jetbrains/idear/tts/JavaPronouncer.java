package com.jetbrains.idear.tts;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.idear.CodeToTextConverter;
import com.jetbrains.idear.tts.TTSService;
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
        int start = editor.getSelectionModel().getSelectionStart();
        int end = editor.getSelectionModel().getSelectionEnd();
        TextRange range = null;
        if (end > start) {
            range = new TextRange(start, end);
        }
        int caretOffset = editor.getCaretModel().getOffset();

        CodeToTextConverter converter = new CodeToTextConverter(psiFile, range, caretOffset);

        TTSService service = ServiceManager.getService(TTSService.class);
        service.say(converter.toText());
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
