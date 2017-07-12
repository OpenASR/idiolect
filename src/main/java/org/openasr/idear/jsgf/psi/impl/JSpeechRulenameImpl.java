// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.*;
import org.openasr.idear.jsgf.JSpeechNamedElementImpl;
import org.openasr.idear.jsgf.psi.*;

public class JSpeechRulenameImpl extends JSpeechNamedElementImpl implements
    JSpeechRulename {

    public JSpeechRulenameImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof JSpeechVisitor)
            ((JSpeechVisitor) visitor).visitRulename(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public PsiElement getString() {
        return findNotNullChildByType(JSpeechTypes.STRING);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws
        IncorrectOperationException {
        return null;
    }
}
