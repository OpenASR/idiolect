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
//        ServiceManager.getService(TTSService.class).init(); //TODO Why does this break TTS? init() is empty...
    }

    @Override
    public void disposeComponent() {
        ServiceManager.getService(ASRService.class).dispose();
//        ServiceManager.getService(TTSService.class).dispose();
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Idear";
    }
}
