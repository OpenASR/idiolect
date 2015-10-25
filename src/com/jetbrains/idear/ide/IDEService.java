package com.jetbrains.idear.ide;

/*

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.Pair;
import com.intellij.util.Consumer;
 */

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;

import java.util.function.Function;

/**
 * Created by breandan on 10/23/2015.
 */
public interface IDEService {
    void init();

    void dispose();

    void type(final int... keys);
    void type(final char... keys);


    void invokeAction(final String action);

    void invokeAction(String action, Function<DataContext, AnActionEvent> actionFactory);
}
