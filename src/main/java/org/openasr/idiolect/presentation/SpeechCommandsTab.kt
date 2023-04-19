package org.openasr.idiolect.presentation

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.SearchTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.util.*
import javax.swing.JComponent
import javax.swing.RowFilter
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableRowSorter


class SpeechCommandsTab(val toolWindow: ToolWindow)
{
    fun getContent(): JComponent {
        var searchField = SearchTextField()
        var table = JBTable(SpeechCommandTableModel())

        val sorter = TableRowSorter(table.model)
        table.rowSorter = sorter

        searchField.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                filterTable()
            }

            override fun removeUpdate(e: DocumentEvent) {
                filterTable()
            }

            override fun changedUpdate(e: DocumentEvent) {
                filterTable()
            }

            private fun filterTable() {
                val text = searchField.text.lowercase(Locale.getDefault())
                sorter.rowFilter = RowFilter.regexFilter("(?i)$text")
            }
        })

        return panel {
            row {
                cell(searchField)
            }
            row {
                scrollCell(table).align(Align.FILL)
            }.resizableRow()
        }
    }

    class SpeechCommandTableModel : AbstractTableModel() {
        private var RESOLVER_EP_NAME = ExtensionPointName<IntentResolver>("org.openasr.idiolect.intentResolver")
        private val grammars = getResolvers().flatMap { it.grammars }
        private val keymap = KeymapManager.getInstance().activeKeymap

        override fun getRowCount(): Int {
            return grammars.size
        }

        override fun getColumnCount(): Int {
            return 3
        }

        override fun getColumnName(column: Int): String {
            return when (column) {
                0 -> "Action"
                1 -> "Examples"
                2 -> "Keyboard Shortcut"
                else -> throw RuntimeException("Incorrect column index")
            }
        }

        override fun getValueAt(row: Int, column: Int): Any {
            val grammar = grammars[row]
            return when (column) {
                0 -> grammar.intentName
                1 -> grammar.examples.joinToString(", ")
                2 -> keymap.getShortcuts(grammar.intentName).joinToString(separator = " or ")
                else -> throw RuntimeException("Incorrect column index")
            }
        }

        private fun getResolvers() = RESOLVER_EP_NAME.extensions
    }
}
