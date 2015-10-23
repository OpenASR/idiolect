package com.jetbrains.idear.ide;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by breandan on 10/23/2015.
 */
public class IDEServiceImpl implements IDEService {
    private static final Logger logger = Logger.getLogger(IDEServiceImpl.class.getSimpleName());

    private Robot robot;

    public void init() {

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dispose() {

    }

    public void pressKeystroke(final int... keys) {
        for (int key : keys) {
            robot.keyPress(key);
        }

        for (int key : keys) {
            robot.keyRelease(key);
        }
    }
}
