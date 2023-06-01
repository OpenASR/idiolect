package org.openasr.idiolect.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VFileProperty
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

object PsiUtil {
    /** open the file, even if it's not already open */
    fun openFileInEditor(project: Project, file: VirtualFile) {
        val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project)
        fileEditorManager.openFile(file, true)
    }

    /** focus if it's already open */
    fun focusFileInEditor(project: Project, file: VirtualFile) {
        val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project)
        val openFile = OpenFileDescriptor(project, file)
        fileEditorManager.openTextEditor(openFile, true)
    }

    /** Should be called from within ApplicationManager.getApplication().runReadAction {} */
    fun getAllFilesInProject(project: Project): List<VirtualFile> {
        val baseDir = project.guessProjectDir() ?: return emptyList()
        val allFiles = mutableListOf<VirtualFile>()

        VfsUtilCore.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<Any>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (file.`is`(VFileProperty.HIDDEN)) {
                    return false
                }
                if (file.isDirectory) {
                    return !(file.name == ".git" || file.name == ".idea")
                }

                if (!file.isDirectory
                    && !file.fileType.isBinary
                    ) {
                    allFiles.add(file)
                }
                return true
            }
        })

        return allFiles
    }

    fun Editor.findElementUnderCaret(): PsiElement? =
        project.let {
            if (it == null) return null
            else PsiDocumentManager.getInstance(it).getPsiFile(document)?.findElementAt(caretModel.offset)
        }

    fun PsiElement.findContainingClass() = PsiTreeUtil.getParentOfType(this, PsiClass::class.java)
}
