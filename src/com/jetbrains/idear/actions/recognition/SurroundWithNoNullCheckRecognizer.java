package com.jetbrains.idear.actions.recognition;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass;
import com.intellij.ide.DataManager;
import com.intellij.ide.actions.ApplyIntentionAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SurroundWithNoNullCheckRecognizer implements ActionRecognizer {

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return sentence.contains("check") && sentence.contains("not");
    }

    @Override
    public ActionCallInfo getActionInfo(@NotNull String sentence, DataContext dataContext) {
        final Editor editor     = CommonDataKeys.EDITOR.getData(dataContext);
        final Project project   = CommonDataKeys.PROJECT.getData(dataContext);

        if (project == null || editor == null) return null;

        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());

        if (file == null) return null;


        final ShowIntentionsPass.IntentionsInfo info = new ShowIntentionsPass.IntentionsInfo();
        ApplicationManager.getApplication().runReadAction(() -> {
            ShowIntentionsPass.getActionsToShow(editor, file, info, -1);
        });
        if (info.isEmpty()) return null;

        final List<HighlightInfo.IntentionActionDescriptor> actions = new ArrayList<>();
        actions.addAll(info.errorFixesToShow);
        actions.addAll(info.inspectionFixesToShow);
        actions.addAll(info.intentionsToShow);

        final ApplyIntentionAction[] result = new ApplyIntentionAction[actions.size()];
        for (int i = 0; i < result.length; i++) {
            final HighlightInfo.IntentionActionDescriptor descriptor = actions.get(i);
            final String actionText = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> descriptor.getAction().getText());
            result[i] = new ApplyIntentionAction(descriptor, actionText, editor, file);
        }

        ApplyIntentionAction nNull = result[1];

        DataManager manager = DataManager.getInstance();
        if (manager != null) {
            DataContext context = manager.getDataContext(editor.getContentComponent());

            nNull.actionPerformed(new AnActionEvent(
                    null, context, "",
                    new Presentation("surround with not null"),
                    ActionManager.getInstance(),
                    0)
            );
        }

        return null;
    }
}


