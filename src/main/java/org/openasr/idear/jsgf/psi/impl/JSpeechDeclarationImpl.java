// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.openasr.idear.jsgf.psi.*;

public class JSpeechDeclarationImpl extends ASTWrapperPsiElement implements
    JSpeechDeclaration {

    public JSpeechDeclarationImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof JSpeechVisitor)
            ((JSpeechVisitor) visitor).visitDeclaration(this);
        else super.accept(visitor);
    }

}
