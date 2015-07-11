package com.jetbrains.idear;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

public class Idear implements ApplicationComponent {


    public Idear() {
    }

    @Override
    public void initComponent() {
        ServiceManager.getService(ASRService.class).init();
        ServiceManager.getService(GrammarService.class).init();
        initTTSService();
    }

    private void initTTSService() {
        PluginId id = PluginId.getId("com.jetbrains.idear");
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);
        assert plugin != null;

        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoader = plugin.getPluginClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            //nasty hack to load TTSService with appropriate constructor
            TTSService service = ServiceManager.getService(TTSService.class);
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    @Override
    public void disposeComponent() {
        ServiceManager.getService(ASRService.class).dispose();
        ServiceManager.getService(TTSService.class).dispose();
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Idear";
    }
}
