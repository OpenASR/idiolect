package org.openasr.idear.jsgf;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import org.openasr.idear.jsgf.psi.JSpeechRulename;

/**
 * Created by breandan on 11/15/2015.
 */

public class JSpeechElementFactory {
    public static JSpeechRulename createProperty(Project project, String name) {
        final JSpeechFile file = createFile(project, name);
        return (JSpeechRulename) file.getFirstChild();
    }

    public static JSpeechFile createFile(Project project, String text) {
        String name = "dummy.simple";
        return (JSpeechFile) PsiFileFactory.getInstance(project).
            createFileFromText(name, JSpeechFileType.INSTANCE, text);
    }
}