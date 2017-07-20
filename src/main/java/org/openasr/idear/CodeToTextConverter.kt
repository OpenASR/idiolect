package org.openasr.idear

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class CodeToTextConverter(myFile: PsiFile, private val myRange: TextRange?, private val myCaretOffset: Int) {
    private val myProject = myFile.project
    private val myDocument = PsiDocumentManager.getInstance(myProject).getDocument(myFile)

    fun toText() =
            myDocument?.run {
                if (myRange != null) return getText(myRange)
                val currentLine = getLineNumber(myCaretOffset)
                val lineStartOffset = getLineStartOffset(currentLine)
                return getText(TextRange(lineStartOffset, currentLine))
            } ?: "Sorry"
}
