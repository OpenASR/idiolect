package com.jetbrains.idear.asr;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Disposer;


/**
 * Created by breandan on 7/10/2015.
 */
public class GrammarService implements Disposable {
    private static final Logger logger = Logger.getInstance(GrammarService.class);

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    public void init() {
        ActionManager actionManager = ActionManager.getInstance();

        actionManager.addAnActionListener(new AnActionListener() {
            @Override
            public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
                String actionId = actionManager.getId(anAction);

                if ("someaction".equals(actionId)) {
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
