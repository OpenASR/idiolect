// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.idear.jsgf.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JSpeechRuleDefinitionImpl extends JSpeechNamedElementImpl implements JSpeechRuleDefinition {

  public JSpeechRuleDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitRuleDefinition(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JSpeechRuleExpansion getRuleExpansion() {
    return findNotNullChildByClass(JSpeechRuleExpansion.class);
  }

  @Override
  @NotNull
  public JSpeechRulename getRulename() {
    return findNotNullChildByClass(JSpeechRulename.class);
  }

  @Override
  @Nullable
  public JSpeechScope getScope() {
    return findChildByClass(JSpeechScope.class);
  }

  public String getKey() {
    return JSpeechImplUtil.getKey(this);
  }

  public String getName() {
    return JSpeechImplUtil.getName(this);
  }

  public PsiElement setName(String newName) {
    return JSpeechImplUtil.setName(this, newName);
  }

  public PsiElement getNameIdentifier() {
    return JSpeechImplUtil.getNameIdentifier(this);
  }

}
