// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface JSpeechRuleAlternative extends PsiElement {

  @Nullable
  JSpeechWeight getWeight();

  @NotNull
  List<JSpeechSequenceElement> getSequenceElementList();

}
