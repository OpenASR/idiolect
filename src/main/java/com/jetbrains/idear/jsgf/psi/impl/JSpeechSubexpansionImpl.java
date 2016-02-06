// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.idear.jsgf.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JSpeechSubexpansionImpl extends ASTWrapperPsiElement implements JSpeechSubexpansion {

  public JSpeechSubexpansionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitSubexpansion(this);
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
