package org.openasr.idear.jsgf;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.*;

public class JSpeechTokenType extends IElementType {

    public JSpeechTokenType(@NotNull @NonNls String debugName) {
        super(debugName, JSpeechLanguage.INSTANCE);
    }
}