package org.openasr.idear.jsgf;


import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by breandan on 11/15/2015.
 */


public abstract class JSpeechNamedElementImpl extends ASTWrapperPsiElement implements JSpeechNamedElement {
    public JSpeechNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}