package com.jetbrains.idear.actions.recognition;

public class ActionCallInfo {
    public Runnable action;

    public String actionId;
    public String typeAfter;

    public ActionCallInfo(String actionId, Runnable action) {
        this.actionId = actionId;
        this.action = action;
    }

    public void setTypeAfter(String typeAfter) {
        this.typeAfter = typeAfter;
    }
}
