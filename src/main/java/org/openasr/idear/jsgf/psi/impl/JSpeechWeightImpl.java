// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.openasr.idear.jsgf.psi.*;

public class JSpeechWeightImpl extends ASTWrapperPsiElement implements
    JSpeechWeight {

  public JSpeechWeightImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitWeight(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getFloat() {
    return findNotNullChildByType(JSpeechTypes.FLOAT);
  }

}
