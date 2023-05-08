package org.openasr.idiolect.presentation.toolwindow.commands

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.ui.JBColor
import com.intellij.ui.SearchTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import org.openasr.idiolect.actions.recognition.CustomPhraseRecognizer
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.util.*
import javax.swing.JComponent
import javax.swing.RowFilter
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableRowSorter


class SpeechCommandsTab : AncestorListener {
    companion object {
        private val SEARCH_BOX_ACCESSIBLE_NAME = "Command search"
        private val log = logger<SpeechCommandsTab>()
    }

    private val commandModel = SpeechCommandTableModel()
    private val table = JBTable(commandModel)
    private val searchField = createSearchComponent(table)


    fun createToolBar(): JComponent {
        return panel {
            row {
                cell(searchField)
                button("Edit Custom Phrases") {
                    IdeService.getProject()?.let {
                        CustomPhraseRecognizer.openCustomPhrasesFile(it)
                    }
                }
            }
        }
    }

    fun createComponent(): JComponent {
        val component = panel {
            row {
                scrollCell(table).align(Align.FILL)
            }.resizableRow()
        }


//        UIUtil.runWhenVisibilityChanged()
        component.addAncestorListener(this)

        return component
    }

    fun getSearchField() = searchField

    private fun createSearchComponent(table: JBTable): SearchTextField {
        val searchField = SearchTextField()
        searchField.accessibleContext.accessibleName = SEARCH_BOX_ACCESSIBLE_NAME
        searchField.background = JBColor.YELLOW

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

        return searchField
    }

    override fun ancestorAdded(event: AncestorEvent?) {
        commandModel.ensureInitialised()
    }

    override fun ancestorRemoved(event: AncestorEvent?) {}

    override fun ancestorMoved(event: AncestorEvent?) {}

    class SpeechCommandTableModel : AbstractTableModel() {
        private var RESOLVER_EP_NAME = ExtensionPointName<IntentResolver>("org.openasr.idiolect.intentResolver")
        private var grammars: List<NlpGrammar> = listOf()
        private val keymap = KeymapManager.getInstance().activeKeymap
        private var initialised = false

        fun ensureInitialised() {
            if (!initialised) {
                update()
                initialised = true
            }
        }

        fun update() {
            Thread({
                log.debug("Generating grammars for speech command tab...")
                grammars = getResolvers().flatMap { it.grammars }
                fireTableDataChanged()
            }, "Idiolect speech commands update").start()
        }

        override fun getRowCount(): Int {
            return grammars.size
        }

        override fun getColumnCount(): Int {
            return 3
        }

        override fun getColumnName(column: Int): String {
            return when (column) {
                0 -> "Action"
                1 -> "Example Phrases"
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
