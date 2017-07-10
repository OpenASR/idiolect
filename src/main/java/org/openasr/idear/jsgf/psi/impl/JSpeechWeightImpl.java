// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.idear.jsgf.psi.JSpeechVisitor;
import com.jetbrains.idear.jsgf.psi.JSpeechWeight;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.idear.jsgf.psi.JSpeechTypes.FLOAT;

public class JSpeechWeightImpl extends ASTWrapperPsiElement implements JSpeechWeight {

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
    return findNotNullChildByType(FLOAT);
  }

}
