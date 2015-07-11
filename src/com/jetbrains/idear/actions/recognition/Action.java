package com.jetbrains.idear.actions.recognition;

public enum Action {

    EXTRACT_VARIABLE("extract", "IntroduceVariable"),
    FIND("find", ""),
    INLINE("inline", "Inline"),
    GOTO("goto", "");

    private final String myActionId;
    private final String myName;

    Action(String name, String actionId) {
        myName = name;
        myActionId = actionId;
    }

    public String getId() {
        return myActionId;
    }

    public String getName() {
        return myName;
    }
}
