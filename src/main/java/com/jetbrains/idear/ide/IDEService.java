package com.jetbrains.idear.ide;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.util.Consumer;

import java.awt.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by breandan on 10/23/2015.
 */
public class IDEService {
    private static final Logger logger = Logger.getLogger(IDEService.class.getSimpleName());

    private Keyboard keyboard;

    public IDEService() {
        try {
            keyboard = new Keyboard();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void init() {

    }

    public void dispose() {

    }

    public AsyncResult<DataContext> invokeAction(final String action) {
        return invokeAction(
                action,
                dataContext ->
                        new AnActionEvent(null,
                                dataContext,
                                ActionPlaces.UNKNOWN,
                                new Presentation(),
                                ActionManager.getInstance(),
                                0
                        )
        );
    }

    public AsyncResult<DataContext> invokeAction(String action, Function<DataContext, AnActionEvent> actionFactory) {
        return DataManager.getInstance().getDataContextFromFocus().doWhenDone(
                (Consumer<DataContext>) dataContext -> EventQueue.invokeLater(() -> {
                    AnAction anAction = ActionManager.getInstance().getAction(action);
                    anAction.actionPerformed(actionFactory.apply(dataContext));
                })
        );
    }

    public void type(final int... keys) {
        keyboard.type(keys);
    }

    public void pressShift() {
        keyboard.pressShift();
    }

    public void releaseShift() {
        keyboard.releaseShift();
    }

    public void type(final char... keys) {
        keyboard.type(keys);
    }

    public void type(final String string) {
        keyboard.type(string);
    }
}
