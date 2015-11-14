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
