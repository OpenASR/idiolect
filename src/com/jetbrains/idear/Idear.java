package com.jetbrains.idear;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

public class Idear implements ApplicationComponent {


    public Idear() {
    }

    @Override
    public void initComponent() {
        ServiceManager.getService(ASRService.class).init();
    }

    @Override
    public void disposeComponent() {
        ServiceManager.getService(ASRService.class).dispose();
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Idear";
    }
}
