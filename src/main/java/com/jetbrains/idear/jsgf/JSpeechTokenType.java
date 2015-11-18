package com.jetbrains.idear.jsgf;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class JSpeechTokenType extends IElementType {

    public JSpeechTokenType(@NotNull @NonNls String debugName) {
        super(debugName, JSpeechLanguage.INSTANCE);
    }
}