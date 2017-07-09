// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.*;
import org.openasr.idear.jsgf.psi.*;

import java.util.List;

public class JSpeechRuleAlternativeImpl extends ASTWrapperPsiElement implements
    JSpeechRuleAlternative {

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
