package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class ActionCallInfo {
    private String actionId;

    private String typeAfter;
    private boolean hitTabAfter;

    public AnActionEvent actionEvent;

    public ActionCallInfo(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() { return actionId; }

    public void setTypeAfter(String typeAfter) {
        this.typeAfter = typeAfter;
    }

    public String getTypeAfter() { return typeAfter; }

    public void setHitTabAfter(boolean value) {
        hitTabAfter = value;
    }

    public boolean getHitTabAfter() { return hitTabAfter; }

    public void setActionEvent(AnActionEvent actionEvent) { this.actionEvent = actionEvent; }

    public AnActionEvent getActionEvent() { return actionEvent; }
}
