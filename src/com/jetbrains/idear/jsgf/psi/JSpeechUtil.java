package com.jetbrains.idear.jsgf.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.idear.jsgf.JSpeechFile;
import com.jetbrains.idear.jsgf.JSpeechFileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by breandan on 11/15/2015.
 */
public class JSpeechUtil {
    public static List<JSpeechRulename> findProperties(Project project, String key) {
        List<JSpeechRulename> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, JSpeechFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            JSpeechFile simpleFile = (JSpeechFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                JSpeechRulename[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, JSpeechRulename.class);
                if (properties != null) {
                    for (JSpeechRulename property : properties) {
                        if (key.equals(property.getText())) {
                            if (result == null) {
                                result = new ArrayList<>();
                            }
                            result.add(property);
                        }
                    }
                }
            }
        }
        return result != null ? result : Collections.<JSpeechRulename>emptyList();
    }

    public static List<JSpeechRulename> findProperties(Project project) {
        List<JSpeechRulename> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, JSpeechFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            JSpeechFile simpleFile = (JSpeechFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                JSpeechRulename[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, JSpeechRulename.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}
