package com.jetbrains.idear;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;

public class CodeToTextConverter {
    public CodeToTextConverter(NavigationItem psiFile, TextRange range, int caret) {
    }

    public String toText() {
        return "Say hi!";
    }
}
