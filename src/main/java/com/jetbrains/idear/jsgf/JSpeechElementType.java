package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class JSpeechElementType extends IElementType {
    public JSpeechElementType(@NotNull @NonNls String debugName) {
        super(debugName, JSpeechLanguage.INSTANCE);
    }
}