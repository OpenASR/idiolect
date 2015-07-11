package com.jetbrains.idear;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.util.Disposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by breandan on 7/10/2015.
 */
public class GrammarServiceImpl implements Disposable, GrammarService {
    private static final Logger logger = LoggerFactory.getLogger(TTSServiceImpl.class);

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    @Override
    public void init() {
        ActionManager actionManager = ActionManager.getInstance();

        actionManager.addAnActionListener(new AnActionListener() {
            @Override
            public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
                String actionId = actionManager.getId(anAction);

                if (actionId == "someaction") {
                    logger.info("Swapping in grammar for action: " + anAction.toString());
                    //swap in a context dependent grammar
                }
            }

            @Override
            public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {

                //swap out a context dependent grammar
            }

            @Override
            public void beforeEditorTyping(char c, DataContext dataContext) {

            }
        });
    }
}
