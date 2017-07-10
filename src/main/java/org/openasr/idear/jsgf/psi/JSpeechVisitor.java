// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.idear.jsgf.JSpeechNamedElement;
import org.jetbrains.annotations.NotNull;

public class JSpeechVisitor extends PsiElementVisitor {

  public void visitSelfIdentHeader(@NotNull JSpeechSelfIdentHeader o) {
    visitPsiElement(o);
  }

  public void visitWeight(@NotNull JSpeechWeight o) {
    visitPsiElement(o);
  }

  public void visitDeclaration(@NotNull JSpeechDeclaration o) {
    visitPsiElement(o);
  }

  public void visitLiteral(@NotNull JSpeechLiteral o) {
    visitPsiElement(o);
  }

  public void visitRuleAlternative(@NotNull JSpeechRuleAlternative o) {
    visitPsiElement(o);
  }

  public void visitRuleDefinition(@NotNull JSpeechRuleDefinition o) {
    visitPsiElement(o);
  }

  public void visitRuleExpansion(@NotNull JSpeechRuleExpansion o) {
    visitPsiElement(o);
  }

  public void visitRulename(@NotNull JSpeechRulename o) {
    visitNamedElement(o);
  }

  public void visitScope(@NotNull JSpeechScope o) {
    visitPsiElement(o);
  }

  public void visitSequenceElement(@NotNull JSpeechSequenceElement o) {
    visitPsiElement(o);
  }

  public void visitSubexpansion(@NotNull JSpeechSubexpansion o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull JSpeechNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
