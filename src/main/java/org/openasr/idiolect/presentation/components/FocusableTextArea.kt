package org.openasr.idiolect.presentation.components

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.border.CompoundBorder
import javax.swing.border.LineBorder

class FocusableTextArea(rows: Int, columns: Int) : JBTextArea(rows, columns), FocusListener {
    private val emptyBorder = JBUI.Borders.empty(2, 10, 2, 2)

    init {
        focusLost(null)
        addFocusListener(this)
    }

    override fun focusGained(e: FocusEvent?) {
        border = CompoundBorder(LineBorder(JBColor.BLUE), emptyBorder)
    }

    override fun focusLost(e: FocusEvent?) {
        border = CompoundBorder(LineBorder(JBColor.LIGHT_GRAY), emptyBorder)
    }
}
