// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.idear.jsgf.psi.JSpeechSequenceElement;
import com.jetbrains.idear.jsgf.psi.JSpeechSubexpansion;
import com.jetbrains.idear.jsgf.psi.JSpeechVisitor;
import org.jetbrains.annotations.NotNull;

public class JSpeechSequenceElementImpl extends ASTWrapperPsiElement implements JSpeechSequenceElement {

  public JSpeechSequenceElementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitSequenceElement(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JSpeechSubexpansion getSubexpansion() {
    return findNotNullChildByClass(JSpeechSubexpansion.class);
  }

}
