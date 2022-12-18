package org.openasr.idear.tts

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.openasr.idear.psi.CodeToTextConverter
import org.openasr.idear.ide.isTextCurrentlySelected

class JavaPronouncer : IntentionAction {

    override fun getText() = "Pronounce"

    override fun getFamilyName() = "Pronounce"

    override fun isAvailable(project: Project, editor: Editor, psiFile: PsiFile) =
        psiFile.language === JavaLanguage.INSTANCE && editor.isTextCurrentlySelected()

    override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        val start = editor.selectionModel.selectionStart
        val end = editor.selectionModel.selectionEnd
        var range: TextRange? = null
        if (end > start) range = TextRange(start, end)
        val caretOffset = editor.caretModel.offset
        val converter = CodeToTextConverter(psiFile, range, caretOffset)

        TTSService.say(converter.toText())
    }

    override fun startInWriteAction() = false
}
