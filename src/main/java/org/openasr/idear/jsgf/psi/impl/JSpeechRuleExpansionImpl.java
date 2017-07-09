// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openasr.idear.jsgf.psi.*;

import java.util.List;

public class JSpeechRuleExpansionImpl extends ASTWrapperPsiElement implements
    JSpeechRuleExpansion {

  public JSpeechRuleExpansionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitRuleExpansion(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JSpeechRuleAlternative> getRuleAlternativeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JSpeechRuleAlternative.class);
  }

}
