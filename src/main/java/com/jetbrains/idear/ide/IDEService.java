package com.jetbrains.idear.ide;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.util.Consumer;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by breandan on 10/23/2015.
 */
public class IDEService {
    private static final Logger logger = Logger.getLogger(IDEService.class.getSimpleName());

    private Robot robot;

    public IDEService() {
        try {
            robot = new Robot();
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
                (Consumer<DataContext>) dataContext -> {
                    try {
                        EventQueue.invokeAndWait(() -> {
                            AnAction anAction = ActionManager.getInstance().getAction(action);
                            anAction.actionPerformed(actionFactory.apply(dataContext));
                        });
                    } catch (InterruptedException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void type(final int... keys) {
        for (int key : keys) {
            robot.keyPress(key);
        }

        for (int key : keys) {
            robot.keyRelease(key);
        }
    }

    public void type(final char... keys) {
        for (int key : keys) {
            robot.keyPress(key);
        }

        for (int key : keys) {
            robot.keyRelease(key);
        }
    }
}
