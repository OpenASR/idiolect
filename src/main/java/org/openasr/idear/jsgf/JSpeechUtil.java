package org.openasr.idear.jsgf;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.openasr.idear.jsgf.psi.JSpeechRuleDefinition;

import java.util.*;

/**
 * Created by breandan on 11/15/2015.
 */
public class JSpeechUtil {
    public static List<JSpeechRuleDefinition> findProperties(Project project, String key) {
        List<JSpeechRuleDefinition> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, JSpeechFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            JSpeechFile simpleFile = (JSpeechFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                JSpeechRuleDefinition[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, JSpeechRuleDefinition.class);
                if (properties != null) {
                    for (JSpeechRuleDefinition property : properties) {
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
        return result != null ? result : Collections.<JSpeechRuleDefinition>emptyList();
    }

    public static List<JSpeechRuleDefinition> findProperties(Project project) {
        List<JSpeechRuleDefinition> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, JSpeechFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            JSpeechFile simpleFile = (JSpeechFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                JSpeechRuleDefinition[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, JSpeechRuleDefinition.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}
