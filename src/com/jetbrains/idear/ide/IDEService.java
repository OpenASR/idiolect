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

/**
 * Created by breandan on 10/23/2015.
 */
public interface IDEService {
    void init();
    void dispose();
    void pressKeystroke(final int... keys);

}
