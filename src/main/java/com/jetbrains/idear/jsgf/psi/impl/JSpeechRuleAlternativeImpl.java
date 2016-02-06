// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.idear.jsgf.psi.JSpeechRuleAlternative;
import com.jetbrains.idear.jsgf.psi.JSpeechSequenceElement;
import com.jetbrains.idear.jsgf.psi.JSpeechVisitor;
import com.jetbrains.idear.jsgf.psi.JSpeechWeight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JSpeechRuleAlternativeImpl extends ASTWrapperPsiElement implements JSpeechRuleAlternative {

  public JSpeechRuleAlternativeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitRuleAlternative(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JSpeechWeight getWeight() {
    return findChildByClass(JSpeechWeight.class);
  }

  @Override
  @NotNull
  public List<JSpeechSequenceElement> getSequenceElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JSpeechSequenceElement.class);
  }

}
