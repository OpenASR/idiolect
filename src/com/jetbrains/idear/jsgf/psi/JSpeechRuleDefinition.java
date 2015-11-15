// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JSpeechRuleDefinition extends JSpeechNamedElement {

  @NotNull
  JSpeechRuleExpansion getRuleExpansion();

  @NotNull
  JSpeechRulename getRulename();

  @Nullable
  JSpeechScope getScope();

  String getKey();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

}
