package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JSpeechFile extends PsiFileBase {
    public JSpeechFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, JSpeechLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return JSpeechFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "JSpeech File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}