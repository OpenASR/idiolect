package com.jetbrains.idear;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by breandan on 6/22/2015.
 */
public class Idear implements ApplicationComponent {


    public Idear() {
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Idear";
    }
}
