package com.jetbrains.idear.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.jetbrains.idear.psi.PsiUtil;
import com.jetbrains.idear.tts.TTSService;

public class WhereAmIAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();

        final Editor editor     = CommonDataKeys.EDITOR.getData(dataContext);
        final Project project   = CommonDataKeys.PROJECT.getData(dataContext);

        PsiElement element = PsiUtil.findElementUnderCaret(editor, project);

        StringBuilder path = new StringBuilder();

        while (null != (element = element.getParent())) {
            if (element instanceof PsiMethod) {
                path.append(" in method ")
                    .append(((PsiMethod)element).getName());
            } else if (element instanceof PsiClass) {
                path.append(" in class ")
                    .append(((PsiClass)element).getName());
            }
        }

        ServiceManager  .getService(TTSService.class)
                        .say("You are" + path.toString());
    }

}
