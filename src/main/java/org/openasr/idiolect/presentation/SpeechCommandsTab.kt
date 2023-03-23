package org.openasr.idiolect.presentation

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.SearchTextField
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.application
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResultListener
import javax.swing.JComponent


class SpeechCommandsTab(val toolWindow: ToolWindow)
{
    fun getContent(): JComponent {
        var searchTextField = SearchTextField()
        var table = JBTable()
        var tableSearch = TableSpeedSearch(table)
//        table.

        return panel {
            row {
                cell(SearchTextField())
            }
            row {
                cell(table) //Search)
            }
        }
    }
}
