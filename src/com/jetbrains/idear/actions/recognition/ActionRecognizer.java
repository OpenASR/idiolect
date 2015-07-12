package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.NotNull;

public interface ActionRecognizer {

    ExtensionPointName<ActionRecognizer> EP_NAME = new ExtensionPointName<>("com.jetbrains.idear.actionRecognizer");

    boolean isMatching(@NotNull String sentence);

    ActionCallInfo getActionInfo(@NotNull String sentence, DataContext dataContext);

}
