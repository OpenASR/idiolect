// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.*;
import org.openasr.idear.jsgf.psi.*;

public class JSpeechSubexpansionImpl extends ASTWrapperPsiElement implements
    JSpeechSubexpansion {

    public JSpeechSubexpansionImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof JSpeechVisitor)
            ((JSpeechVisitor) visitor).visitSubexpansion(this);
        else super.accept(visitor);
    }

    @Override
    @Nullable
    public JSpeechLiteral getLiteral() {
        return findChildByClass(JSpeechLiteral.class);
    }

    @Override
    @Nullable
    public JSpeechRuleExpansion getRuleExpansion() {
        return findChildByClass(JSpeechRuleExpansion.class);
    }

    @Override
    @Nullable
    public JSpeechRulename getRulename() {
        return findChildByClass(JSpeechRulename.class);
    }

}
