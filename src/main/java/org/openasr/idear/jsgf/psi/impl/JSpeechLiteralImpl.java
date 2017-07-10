// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.idear.jsgf.psi.JSpeechLiteral;
import com.jetbrains.idear.jsgf.psi.JSpeechVisitor;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.idear.jsgf.psi.JSpeechTypes.STRING;

public class JSpeechLiteralImpl extends ASTWrapperPsiElement implements JSpeechLiteral {

  public JSpeechLiteralImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JSpeechVisitor) ((JSpeechVisitor)visitor).visitLiteral(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getString() {
    return findNotNullChildByType(STRING);
  }

}
