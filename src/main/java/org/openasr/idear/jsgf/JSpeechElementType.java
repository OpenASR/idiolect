package org.openasr.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.*;

public class JSpeechElementType extends IElementType {
    public JSpeechElementType(@NotNull @NonNls String debugName) {
        super(debugName, JSpeechLanguage.INSTANCE);
    }
}