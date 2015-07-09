package com.jetbrains.idear;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadPoolExecutor;

public class JavaPronouncer implements IntentionAction {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Pronounce";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Pronounce";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        SelectionModel selectionModel = editor.getSelectionModel();
        return psiFile.getLanguage() == JavaLanguage.INSTANCE && selectionModel.hasSelection();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        int start = editor.getSelectionModel().getSelectionStart();
        int end = editor.getSelectionModel().getSelectionEnd();
        TextRange range = null;
        if (end > start) {
            range = new TextRange(start, end);
        }
        int caretOffset = editor.getCaretModel().getOffset();

        CodeToTextConverter converter = new CodeToTextConverter(psiFile, range, caretOffset);

        PluginId id = PluginId.getId("com.jetbrains.idear");
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);
        assert plugin != null;

        ClassLoader prev = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoader = plugin.getPluginClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            TextToSpeechService service = ServiceManager.getService(TextToSpeechService.class);
            service.say(converter.toText());
        } finally {
            Thread.currentThread().setContextClassLoader(prev);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
