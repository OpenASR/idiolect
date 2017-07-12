package org.openasr.idear.jsgf;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.*;

/**
 * Created by breandan on 11/13/2015.
 */
public class JSpeechSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project,
                                                  @Nullable VirtualFile
                                                      virtualFile) {
        return new JSpeechSyntaxHighlighter();
    }
}
