package com.jetbrains.idear.ide;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.util.Consumer;

import java.awt.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by breandan on 10/23/2015.
 */
public class IDEServiceImpl implements IDEService {
    private static final Logger logger = Logger.getLogger(IDEServiceImpl.class.getSimpleName());

    private Robot robot;

    public IDEServiceImpl() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void dispose() {

    }

    public void invokeAction(final String action) {
        invokeAction(
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

    public void invokeAction(String action, Function<DataContext, AnActionEvent> actionFactory) {
        DataManager.getInstance().getDataContextFromFocus().doWhenDone(
                (Consumer<DataContext>) dataContext -> EventQueue.invokeLater(() -> {
                    AnAction anAction = ActionManager.getInstance().getAction(action);
                    anAction.actionPerformed(actionFactory.apply(dataContext));
                })
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
