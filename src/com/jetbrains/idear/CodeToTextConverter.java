package com.jetbrains.idear;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeToTextConverter {
    private final Project myProject;
    private final PsiFile myFile;
    private final TextRange myRange;
    private final int myCaretOffset;
    private final Document myDocument;

    public CodeToTextConverter(@NotNull PsiFile psiFile, @Nullable TextRange range, int caret) {
        myProject = psiFile.getProject();
        myFile = psiFile;
        myDocument = PsiDocumentManager.getInstance(myProject).getDocument(myFile);
        myRange = range;
        myCaretOffset = caret;
    }

    public String toText() {
        if (myDocument == null) {
            return "Sorry";
        }

        if (myRange != null) {
            return myDocument.getText(myRange);
        }

        int currentLine = myDocument.getLineNumber(myCaretOffset);
        int lineStartOffset = myDocument.getLineStartOffset(currentLine);

        return myDocument.getText(new TextRange(lineStartOffset, currentLine));
    }
}
