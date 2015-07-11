package com.jetbrains.idear.actions.recognition;

import org.jetbrains.annotations.NotNull;

public interface ActionRecognizer {

    boolean isMatching(@NotNull String sentence);

    ActionCallInfo getActionInfo(@NotNull String sentence);

}
