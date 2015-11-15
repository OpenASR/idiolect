// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.jetbrains.idear.jsgf.psi.JSpeechTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.jetbrains.idear.jsgf.psi.*;
import com.jetbrains.idear.jsgf.JSpeechPsiImplUtil;

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
