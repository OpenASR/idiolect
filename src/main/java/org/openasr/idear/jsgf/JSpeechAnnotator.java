package com.jetbrains.idear.jsgf;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.jetbrains.idear.jsgf.psi.JSpeechLiteral;
import com.jetbrains.idear.jsgf.psi.JSpeechRulename;
import com.jetbrains.idear.jsgf.psi.JSpeechScope;
import org.jetbrains.annotations.NotNull;

/**
 * Created by breandan on 11/14/2015.
 */
public class JSpeechAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
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
            TextAttributesKey key = JSpeechSyntaxHighlighter.KEYS.get(keyString);
            assert key != null : keyString;
            holder.createInfoAnnotation(element, null).setTextAttributes(key);
        }
    }
}
