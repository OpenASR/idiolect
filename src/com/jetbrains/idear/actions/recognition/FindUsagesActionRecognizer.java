package com.jetbrains.idear.actions.recognition;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.find.FindManager;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import com.jetbrains.idear.psi.PsiUtil;
import com.jetbrains.idear.tts.TTSService;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

//runs only selected configuration
public class FindUsagesActionRecognizer implements ActionRecognizer {

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return sentence.contains("find") /* && (sentence.contains("usages") || sentence.contains("usage")) */;
    }

    @Override
    public ActionCallInfo getActionInfo(@NotNull String sentence, DataContext dataContext) {
        ActionCallInfo aci = new ActionCallInfo("FindUsages");

        // Ok, that's lame
        List<String>    words       = Arrays.asList(sentence.split("\\W+"));
        HashSet<String> wordsSet    = new HashSet<>(words);

        final Editor editor     = CommonDataKeys.EDITOR.getData(dataContext);
        final Project project   = CommonDataKeys.PROJECT.getData(dataContext);

        if (editor == null || project == null)
            return aci;

        final PsiElement    e       = PsiUtil.findElementUnderCaret(editor, project);
        final PsiClass      klass   = PsiUtil.findContainingClass(e);

        if (klass == null)
            return aci;

        PsiElement[] targets = null;

        String targetName   = null;
        String subject      = null;

        if (wordsSet.contains("field")) {
            subject     = "field";
            targetName  = extractNameOf("field", words);

            if (targetName.isEmpty())
                return aci;

            targets = new PsiElement[] { klass.findFieldByName(targetName, /*checkBases*/ true) };

        } else if (wordsSet.contains("method")) {
            subject     = "method";
            targetName  = extractNameOf("method", words);

            if (targetName.isEmpty())
                return aci;

            targets = klass.findMethodsByName(targetName, /*checkBases*/ true);
        }

        if (targets == null)
            return aci;

        // TODO(kudinkin): need to cure this pain someday

        aci.setActionEvent(
            new AnActionEvent(
                null,
                SimpleDataContext.getSimpleContext(UsageView.USAGE_TARGETS_KEY.getName(), prepare(targets[0]), dataContext),
                ActionPlaces.UNKNOWN,
                new Presentation(),
                ActionManager.getInstance(),
                0
                )
        );

        // TODO(kudinkin): move it to appropriate place
        ServiceManager  .getService(TTSService.class)
                        .say("Looking for usages of the " + subject + " " + targetName);

        return aci;
    }

    private static UsageTarget[] prepare(PsiElement target) {
        return new UsageTarget[] { new PsiElement2UsageTargetAdapter(target) };
    }

    private static String extractNameOf(String pivot, List<String> sentence) {
        StringBuilder target = new StringBuilder();

        for (int i = sentence.indexOf(pivot) + 1; i < sentence.size(); ++i) {
            target.append(sentence.get(i));
        }

        return target.toString();
    }
}
