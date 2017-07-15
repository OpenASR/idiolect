package org.openasr.idear.jsgf;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.openasr.idear.jsgf.psi.*;

/**
 * Created by breandan on 11/14/2015.
 */
public class JSpeechAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element,
                         @NotNull AnnotationHolder holder) {
        PsiElement parent = element.getParent();
        String keyString = null;

        if (parent instanceof JSpeechScope) {
            keyString = "TYPE_REF";
        } else if (parent instanceof JSpeechRulename) {
            keyString = "OPERATION_NAME";
        } else if (parent instanceof JSpeechLiteral) {
            keyString = "FIELD_NAME";
        }

        if (keyString != null) {
            TextAttributesKey key =
                JSpeechSyntaxHighlighter.KEYS.get(keyString);
            assert key != null : keyString;
            holder.createInfoAnnotation(element, null).setTextAttributes(key);
        }
    }
}
