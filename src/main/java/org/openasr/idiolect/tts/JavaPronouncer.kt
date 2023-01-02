package org.openasr.idiolect.tts

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.openasr.idiolect.ide.isTextCurrentlySelected
import org.openasr.idiolect.psi.CodeToTextConverter

class JavaPronouncer : IntentionAction {

    override fun getText() = "Pronounce"

    override fun getFamilyName() = "Pronounce"

    override fun isAvailable(project: Project, editor: Editor, psiFile: PsiFile) =
        psiFile.language === JavaLanguage.INSTANCE && editor.isTextCurrentlySelected()

    override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        val start = editor.selectionModel.selectionStart
        val end = editor.selectionModel.selectionEnd
        val range = if (start < end) TextRange(start, end) else null
        val converter = CodeToTextConverter(psiFile, range, editor.caretModel.offset)

        TtsService.say(converter.toText())
    }

    override fun startInWriteAction() = false
}
