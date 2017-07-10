package org.openasr.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class JSpeechFileType extends LanguageFileType {
    public static final JSpeechFileType INSTANCE = new JSpeechFileType();

    private JSpeechFileType() {
        super(JSpeechLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "JSpeech file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "JSpeech language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "gram";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return JSpeechIcons.FILE;
    }
}

